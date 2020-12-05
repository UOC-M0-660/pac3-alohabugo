package edu.uoc.pac3.data

import android.util.Log
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.data.user.UserResponse
import io.ktor.client.*
import io.ktor.client.request.*
import kotlin.Exception

/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient) {
    private val TAG = "TwitchApiService"

    /// Gets Access and Refresh Tokens on Twitch
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? {
        // TODO("Get Tokens from Twitch")
        // solicitud POST al /token Endpoint disponible de OAuth con los siguientes parámetros
        val response = httpClient.post<OAuthTokensResponse>(Endpoints.epToken) {
            // parameter("client_id", OAuthConstants.clientID)
            parameter("client_secret", OAuthConstants.clientSecret)
            parameter("code", authorizationCode)
            parameter("grant_type", "authorization_code")
            parameter("redirect_uri", OAuthConstants.redirectUri)
        }
        Log.d(TAG, "Access Token: ${response.accessToken}. Refresh Token: ${response.refreshToken}")

        return response

    }

    suspend fun getRefreshToken(refreshToken: String): OAuthTokensResponse? {
        // solicitud POST para refreshToken
        try {
            return httpClient.post<OAuthTokensResponse>(Endpoints.epToken) {
                parameter("client_secret", OAuthConstants.clientSecret)
                parameter("refresh_token", refreshToken)
                parameter("grant_type", "refresh_token")
            }
        } catch (e: Exception) {
            throw UnauthorizedException
        }
    }

    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null): StreamsResponse? {
        // TODO("Get Streams from Twitch")
        // solicitud GET para obtener los streams de Twitch
        try {
            if (cursor == null) {
                return httpClient.get<StreamsResponse>(Endpoints.epStreams)
            } else {
                // TODO("Support Pagination")
                // pasamos cursor como parámetro para la paginación
                return httpClient.get<StreamsResponse>(Endpoints.epStreams) {
                    parameter("first", 20)
                    parameter("after", cursor)
                }
            }
        } catch (e: Exception) {
            throw UnauthorizedException
        }
    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getUser(): User? {
        // TODO("Get User from Twitch")
        // obtenemos la lista de usuarios logeados
        val response = httpClient.get<UserResponse>(Endpoints.epUser)
        Log.d(TAG, "User: ${response.data?.get(0)}")
        // devolvemos el primer usuario de la lista que corresponde al usuario logeado
        return response.data?.get(0)
    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun updateUserDescription(description: String): User? {
        // TODO("Update User Description on Twitch")
        // se acutaliza la descripción del usuario en Twitch
        val response = httpClient.put<UserResponse>(Endpoints.epUser) {
            parameter("description", description)
        }
        // devolvemos el primer usuario de la lista que corresponde al usuario logeado
        return response.data?.get(0)
    }
}