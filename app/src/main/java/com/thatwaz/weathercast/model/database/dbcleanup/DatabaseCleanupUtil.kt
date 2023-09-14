package com.thatwaz.weathercast.model.database.dbcleanup

import com.thatwaz.weathercast.model.database.WeatherDatabase


object DatabaseCleanupUtil {

    suspend fun cleanupCurrentWeatherDatabase(
        weatherDatabase: WeatherDatabase,
        maxEntries: Int = 1
    ) {
        val initialCount = weatherDatabase.weatherDataDao().getCount()

        var currentCount = initialCount

        while (currentCount > maxEntries) {
            weatherDatabase.weatherDataDao().deleteOldestEntry()
            currentCount = weatherDatabase.weatherDataDao().getCount()
        }

    }


    suspend fun cleanupHourlyWeatherDatabase(
        weatherDatabase: WeatherDatabase,
        maxEntries: Int = 1
    ) {
        val initialCount = weatherDatabase.hourlyWeatherDao().getCount()

        var currentCount = initialCount

        while (currentCount > maxEntries) {
            weatherDatabase.hourlyWeatherDao().deleteOldestEntry()
            currentCount = weatherDatabase.hourlyWeatherDao().getCount()
        }

    }

    suspend fun cleanupForecastWeatherDatabase(
        weatherDatabase: WeatherDatabase,
        maxEntries: Int = 1
    ) {
        val initialCount = weatherDatabase.forecastDao().getCount()

        var currentCount = initialCount

        while (currentCount > maxEntries) {
            weatherDatabase.forecastDao().deleteOldestEntry()
            currentCount = weatherDatabase.forecastDao().getCount()
        }

    }
}