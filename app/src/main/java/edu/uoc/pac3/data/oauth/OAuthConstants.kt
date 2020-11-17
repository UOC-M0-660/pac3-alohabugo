package edu.uoc.pac3.data.oauth

import java.util.*

/**
 * Created by alex on 07/09/2020.
 */
object OAuthConstants {

    // TODO: Set OAuth2 Variables

    // URL expuesta por el proveedor de OAuth para obtener el código de autorización
    const val authorizationUrl = "https://id.twitch.tv/oauth2/authorize"
    // ID de cliente
    const val clientID = "ss165d4of888691m1mg537amr814fx";
    // ID secreto de cliente
    const val clientSecret = "waovvobuzdz8ppftw4sxvxblglx0cr";
    // URL de redireccionamiento de OAuth
    const val redirectUri = "http://localhost";
    // variable de cadena única state, para evitar ataques CSRF
    val uniqueState = UUID.randomUUID().toString()
    // lista que contiene todos los permisos para solicitar
    val scopes: List<String> = listOf("user:read:email", "user:edit")

}