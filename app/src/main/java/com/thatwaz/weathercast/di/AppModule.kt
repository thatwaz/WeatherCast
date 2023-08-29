package com.thatwaz.weathercast.di

import android.content.Context
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import dagger.Module
import dagger.Provides



@Module
class AppModule(private val application: WeatherCastApplication) {

    @Provides
    fun provideApplication(): WeatherCastApplication = application

    @Provides
    fun provideContext(): Context = application.applicationContext

}



