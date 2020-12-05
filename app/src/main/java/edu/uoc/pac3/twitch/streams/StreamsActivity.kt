package edu.uoc.pac3.twitch.streams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.oauth.LoginActivity
import edu.uoc.pac3.twitch.profile.ProfileActivity
import io.ktor.client.features.*
import kotlinx.coroutines.launch

class StreamsActivity : AppCompatActivity() {

    private val TAG = "StreamsActivity"
    private lateinit var twitchService: TwitchApiService
    private lateinit var adapter: StreamsListAdapter
    private lateinit var sessionManag: SessionManager
    // paginacion
    private var cursor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)
        // Init RecyclerView
        initRecyclerView()
        // Inicializamos variable twitchService
        twitchService = TwitchApiService(Network.createHttpClient(this))
        // Inicializamos variable sessionManag
        sessionManag = SessionManager(this)

        // TODO: Get Streams
        lifecycleScope.launch {
            try {
                // obtener los streams
                getStreams()
            } catch (e: UnauthorizedException) {
                try {
                    // excepction "401 Unautorized"
                    Log.d(TAG, "UnauthorizedException: refreshAccessToken")
                    // refrescamos token
                    refreshAccessToken()
                    // volvemos a realizar la petición
                    getStreams()
                } catch (e: UnauthorizedException) {
                    // limpiamos tokens
                    Log.d(TAG, "UnauthorizedException: clear tokens")
                    sessionManag.clearAccessToken()
                    sessionManag.clearRefreshToken()
                    // volvemos a la pantalla de login
                    startActivity(Intent(this@StreamsActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }

    }

    private fun initRecyclerView() {
        // TODO: Implement
        val mRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set Layout Manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = StreamsListAdapter(ArrayList())
        mRecyclerView.adapter = adapter
        // Paginacion - solicitar los siguientes 20 streams
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // Si se llega al final de la lista y no se puede realizar scroll vertical
                // y ademas el nuevo estado es de no desplazamiento
                if (!mRecyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // solicitamos streams
                    getStreams()
                }
            }
        })
    }

    private fun getStreams() {
        lifecycleScope.launch {
            // obtener el listado de streams y mostrarlos en recyclerView
            val streams = twitchService.getStreams(cursor)
            streams?.data?.let {
                adapter.addStreams(it)
            }
            // paginacion
            streams?.pagination?.cursor?.let {
                cursor = it
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // opciones del menu main_menu
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // open activity: ProfileActivity
            R.id.menu_item_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}