package com.developmentwords.developmentwords.util

import android.app.Application

class PreferenceSingleton : Application() {
    companion object{
        lateinit var prefs: PreferenceUtil
    }

    override fun onCreate() {
        prefs = PreferenceUtil(applicationContext)
        super.onCreate()
    }
}