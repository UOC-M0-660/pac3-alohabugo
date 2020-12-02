package edu.uoc.pac3.twitch.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import edu.uoc.pac3.LaunchActivity
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.user.User
import io.ktor.client.features.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private val TAG = "ProfileActivity"

    private lateinit var twitchService: TwitchApiService
    private lateinit var sessionManag: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializamos variable twitchService
        twitchService = TwitchApiService(Network.createHttpClient(this))
        // Inicializamos variable sessionManag
        sessionManag = SessionManager(this)

        // Obtener los datos del usuario logueado
        lifecycleScope.launch {
            try {
                getUserData()
            } catch (e: UnauthorizedException) {
                try {
                    // excepction "No autorizado"
                    Log.d(TAG, "UnauthorizedException: refreshAccessToken")
                    // refrescamos token
                    refreshAccessToken()
                    // volvemos a realizar la petición
                    getUserData()
                } catch (e: UnauthorizedException) {
                    // limpiamos tokens
                    Log.d(TAG, "UnauthorizedException: clear tokens")
                    sessionManag.clearAccessToken()
                    sessionManag.clearRefreshToken()
                    // volvemos a la pantalla inicial
                    startActivity(Intent(this@ProfileActivity, LaunchActivity::class.java))
                }
            }
        }
        // listener para los botones
        updateDescriptionButton.setOnClickListener { updateUserDescription() }
        logoutButton.setOnClickListener { logout() }

    }

    private fun getUserData() {
        lifecycleScope.launch {
            // obtener datos del usuario logeado y mostrarlos
            val user = twitchService.getUser()
            showUserProfile(user)
        }
    }

    private fun showUserProfile(user: User?) {
        // mostramos imagen, nombre y descripción
        user?.let {
            Glide.with(this)
                    .load(it.profileImageUrl)
                    .into(imageView)
            userNameTextView.text = it.userName
            userDescriptionEditText.setText(it.description)
            viewsText.text = resources.getString(R.string.views_text, it.viewCount)
        }
    }

    private fun updateUserDescription() {
        // guardamos cambios en Twitch y volvemos a mostrar
        val des = userDescriptionEditText.text.toString()
        lifecycleScope.launch {
            val user = twitchService.updateUserDescription(des)
            showUserProfile(user)
        }
    }

    private fun logout() {
        // eliminar la sesión y hacer login de nuevo
        lifecycleScope.launch {
            sessionManag.clearAccessToken()
            sessionManag.clearRefreshToken()
        }
        startActivity(Intent(this, LaunchActivity::class.java))
    }

    private suspend fun refreshAccessToken() {
        // refresh token using the SessionManager class
        try {
            val refreshToken = sessionManag.getRefreshToken()
            val response = refreshToken?.let { twitchService.getRefreshToken(it) }
            response?.accessToken?.let { sessionManag.saveAccessToken(it) }
            response?.refreshToken?.let { sessionManag.saveRefreshToken(it) }
        } catch (e: ClientRequestException) {
            Log.d(TAG, "Error refreshing token")
        }
    }

}