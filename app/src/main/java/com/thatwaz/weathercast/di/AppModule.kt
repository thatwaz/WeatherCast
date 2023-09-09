package com.thatwaz.weathercast.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import dagger.Module
import dagger.Provides


@Module
class AppModule(private val application: WeatherCastApplication) {

    @Provides
    fun provideApplication(): WeatherCastApplication = application

    @Provides
    fun provideContext(): Context = application.applicationContext

    @Provides
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

}



