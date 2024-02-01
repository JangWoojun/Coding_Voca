package com.developmentwords.developmentwords.util

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_data", Context.MODE_PRIVATE)

    fun setString(id: String, str: String) {
        prefs.edit().putString(id, str).apply()
    }

    fun getString(id: String): String {
        return prefs.getString(id, "0").toString()
    }

    fun removeString() {
        prefs.edit().clear().commit()
    }

}