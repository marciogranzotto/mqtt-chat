package com.granzotto.mqttchat

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {

    private const val SHARED_PREFERENCES_KEY = "mqtt_chat_key"

    private var sharedPreferences: SharedPreferences? = null

    fun setup(context: Context) {
        sharedPreferences =
            context.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    fun clearPreferences() {
        sharedPreferences?.edit()?.clear()?.apply()
    }

    var username: String?
        get() = sharedPreferences?.getString("username", null)
        set(value) {
            sharedPreferences
                ?.edit()
                ?.putString("username", value)
                ?.apply()
        }
}