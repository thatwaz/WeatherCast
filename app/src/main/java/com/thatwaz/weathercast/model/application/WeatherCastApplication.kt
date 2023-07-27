package com.thatwaz.weathercast.model.application

import android.app.Application

class WeatherCastApplication : Application() {

    companion object {
        lateinit var instance: WeatherCastApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
