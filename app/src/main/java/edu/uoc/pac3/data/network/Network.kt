package edu.uoc.pac3.data.network

import android.content.Context
import android.util.Log
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.oauth.OAuthConstants
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.Json

/**
 * Created by alex on 07/09/2020.
 */
object Network {

    private const val TAG = "Network"

    // punto de partida para cada solicitud de red, Ktor Http Client
    fun createHttpClient(context: Context): HttpClient {
        // obtenemos accessToken almacenado en la sesion
        val accessToken = SessionManager(context).getAccessToken()

        return HttpClient(OkHttp) {
            // TODO: Setup HttpClient
            // Json
            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
            // Logging
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Ktor", message)
                    }
                }
                level = LogLevel.ALL
            }
            // Timeout
            install(HttpTimeout) {
                requestTimeoutMillis = 15000L
                connectTimeoutMillis = 15000L
                socketTimeoutMillis = 15000L
            }
            // Apply to All Requests
            defaultRequest {
                // parameter("api_key", "some_api_key")
                // añadir el cliente ID a todas las peticiones
                header("Client-Id", OAuthConstants.clientID)
                // añadir el accessToken a todas las peticiones
                if (accessToken != null) {
                    //encabezado de autorización
                    header("Authorization", "Bearer $accessToken")
                }
                // Content Type
                if (this.method != HttpMethod.Get) contentType(ContentType.Application.Json)

                accept(ContentType.Application.Json)
            }
        }
    }

    private val json = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }

}