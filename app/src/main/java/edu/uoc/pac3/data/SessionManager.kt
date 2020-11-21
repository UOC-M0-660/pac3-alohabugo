package edu.uoc.pac3.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by alex on 06/09/2020.
 */

class SessionManager(context: Context) {

    // variable para almacenar las SharedPreferences
    private val mSharedPref = context.getSharedPreferences("tokenKey", Context.MODE_PRIVATE)

    fun isUserAvailable(): Boolean {
        // TODO: Implement
        // return false
        return mSharedPref.getString("accessTokenKey", null) != null
    }

    fun getAccessToken(): String? {
        // TODO: Implement
        return mSharedPref.getString("accessTokenKey", null)
    }

    fun saveAccessToken(accessToken: String) {
        // TODO("Save Access Token")
        // uso de apply() para guardar los cambios
        mSharedPref?.edit()?.putString("accessTokenKey", accessToken)?.apply()
    }

    fun clearAccessToken() {
        // TODO("Clear Access Token")
        // uso de apply() para limpiar access token
        mSharedPref.edit().remove("accessTokenKey").apply()
    }

    fun getRefreshToken(): String? {
        // TODO("Get Refresh Token")
        return mSharedPref.getString("refreshTokenKey", null)
    }

    fun saveRefreshToken(refreshToken: String) {
        // TODO("Save Refresh Token")
        // uso de apply() para guardar los cambios
        mSharedPref?.edit()?.putString("refreshTokenKey", refreshToken)?.apply()
    }

    fun clearRefreshToken() {
        // TODO("Clear Refresh Token")
        // uso de apply() para limpiar refresh token
        mSharedPref.edit().remove("refreshTokenKey").apply()
    }

}