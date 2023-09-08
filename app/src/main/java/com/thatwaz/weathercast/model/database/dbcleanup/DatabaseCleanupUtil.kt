package com.thatwaz.weathercast.model.database.dbcleanup

import android.util.Log
import com.thatwaz.weathercast.model.database.WeatherDatabase


object DatabaseCleanupUtil {

    /* Set max entries to 10 to allow caching of responses for multiple locations, especially useful
    when testing with mock locations to simulate different geographic areas. */

    suspend fun cleanupCurrentWeatherDatabase(
        weatherDatabase: WeatherDatabase,
        maxEntries: Int = 10
    ) {
        val initialCount = weatherDatabase.weatherDataDao().getCount()
        Log.i("MOH!", "Before: Total number of entries: $initialCount")

        var currentCount = initialCount

        while (currentCount > maxEntries) {
            weatherDatabase.weatherDataDao().deleteOldestEntry()
            currentCount = weatherDatabase.weatherDataDao().getCount()
        }

        Log.i("MOH!", "After: Total number of entries: $currentCount")
    }


    suspend fun cleanupHourlyWeatherDatabase(
        weatherDatabase: WeatherDatabase,
        maxEntries: Int = 10
    ) {
        val initialCount = weatherDatabase.hourlyWeatherDao().getCount()
        Log.i("MOH!", "Before: Total number of entries: $initialCount")

        var currentCount = initialCount

        while (currentCount > maxEntries) {
            weatherDatabase.hourlyWeatherDao().deleteOldestEntry()
            currentCount = weatherDatabase.hourlyWeatherDao().getCount()
        }

        Log.i("MOH!", "After: Total number of entries: $currentCount")
    }

    suspend fun cleanupForecastWeatherDatabase(
        weatherDatabase: WeatherDatabase,
        maxEntries: Int = 10
    ) {
        val initialCount = weatherDatabase.forecastDao().getCount()
        Log.i("MOH!", "Before: Total number of entries: $initialCount")

        var currentCount = initialCount

        while (currentCount > maxEntries) {
            weatherDatabase.forecastDao().deleteOldestEntry()
            currentCount = weatherDatabase.forecastDao().getCount()
        }

        Log.i("MOH!", "After: Total number of entries: $currentCount")
    }
}