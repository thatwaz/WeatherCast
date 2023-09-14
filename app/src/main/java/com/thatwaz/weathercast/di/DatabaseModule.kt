package com.thatwaz.weathercast.di

import android.content.Context
import androidx.room.Room
import com.thatwaz.weathercast.model.database.WeatherDatabase
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    fun provideWeatherDatabase(context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            WeatherDatabase::class.java,
            "weather_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}

