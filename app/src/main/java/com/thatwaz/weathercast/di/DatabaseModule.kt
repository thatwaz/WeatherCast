package com.thatwaz.weathercast.di

import android.content.Context
import com.thatwaz.weathercast.model.database.WeatherDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    fun provideWeatherDatabase(context: Context): WeatherDatabase {
        return WeatherDatabase.getInstance(context)
    }
}
