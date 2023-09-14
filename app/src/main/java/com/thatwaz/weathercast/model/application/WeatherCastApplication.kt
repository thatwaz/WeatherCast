package com.thatwaz.weathercast.model.application



import android.app.Application
import com.thatwaz.weathercast.di.AppComponent
import com.thatwaz.weathercast.di.AppModule
import com.thatwaz.weathercast.di.DaggerAppComponent


class WeatherCastApplication : Application() {

    lateinit var appComponent: AppComponent

    companion object {
        lateinit var instance: WeatherCastApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}



