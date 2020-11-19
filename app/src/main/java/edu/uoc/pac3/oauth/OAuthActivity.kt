package edu.uoc.pac3.oauth

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.OAuthConstants
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class OAuthActivity : AppCompatActivity() {

    private val TAG = "OAuthActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        launchOAuthAuthorization()
    }

    fun buildOAuthUri(): Uri {
        // TODO: Create URI
        // Creación de la solicitud de URL
        val uri = Uri.parse(OAuthConstants.authorizationUrl)
                .buildUpon()
                .appendQueryParameter("client_id", OAuthConstants.clientID)
                .appendQueryParameter("redirect_uri", OAuthConstants.redirectUri)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope", OAuthConstants.scopes.joinToString(separator = " "))
                .appendQueryParameter("state", OAuthConstants.uniqueState)
                .build()
        return uri
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun launchOAuthAuthorization() {
        //  Create URI
        val uri = buildOAuthUri()

        // TODO: Set webView Redirect Listener
        // Configurar WebViewClient para interceptar la redirección y analizar la URL
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.let {
                    // Check if this url is our OAuth redirect, otherwise ignore it
                    // Verifique si esta URL es nuestra redirección de OAuth; de lo contrario, ignórela
                    if (request.url.toString().startsWith(OAuthConstants.redirectUri)) {
                        // To prevent CSRF attacks, check that we got the same state value we sent, otherwise ignore it
                        // Para evitar ataques CSRF, verifique que tengamos el mismo valor de estado que enviamos, de lo contrario ignórelo
                        val responseState = request.url.getQueryParameter("state")
                        if (responseState == OAuthConstants.uniqueState) {
                            // This is our request, obtain the code!
                            // Esta es nuestra solicitud, obtenga el código!
                            request.url.getQueryParameter("code")?.let { code ->
                                // Got it!
                                Log.d("OAuth", "Here is the authorization code! $code")
                                // pasamos el codigo de autorización para obtener los tokens
                                onAuthorizationCodeRetrieved(code)
                            } ?: run {
                                // User cancelled the login flow
                                // TODO: Handle error
                                Log.d("OAuth", "User cancelled the login flow")
                            }
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }

        // Load OAuth Uri
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(uri.toString())
    }

    // Call this method after obtaining the authorization code
    // on the WebView to obtain the tokens
    private fun onAuthorizationCodeRetrieved(authorizationCode: String) {

        // Show Loading Indicator
        progressBar.visibility = View.VISIBLE

        // TODO: Create Twitch Service
        val twitchService = TwitchApiService(Network.createHttpClient(this))

        // TODO: Get Tokens from Twitch
        lifecycleScope.launch {
            val tokens = twitchService.getTokens(authorizationCode)

            Log.d(TAG, "Access Token: ${tokens?.accessToken}. Refresh Token: ${tokens?.refreshToken}")

            // TODO: Save access token and refresh token using the SessionManager class
            tokens?.let {
                val sessionManag = SessionManager(this@OAuthActivity)
                sessionManag.saveAccessToken(it.accessToken)
                if (it.refreshToken != null) { sessionManag.saveRefreshToken(it.refreshToken) }
                }
        }
    }
}