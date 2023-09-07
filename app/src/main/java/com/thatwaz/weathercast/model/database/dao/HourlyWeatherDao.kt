package com.thatwaz.weathercast.model.database.dao

import androidx.room.*
import com.thatwaz.weathercast.model.database.entities.HourlyWeatherEntity

@Dao
interface HourlyWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeather: HourlyWeatherEntity)

    @Query("SELECT * FROM hourly_weather_data WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun getHourlyWeather(latitude: Double, longitude: Double): HourlyWeatherEntity?

    @Query("DELETE FROM hourly_weather_data WHERE id = (SELECT MIN(id) FROM hourly_weather_data)")
    suspend fun deleteOldestEntry()

    @Query("SELECT COUNT(*) FROM hourly_weather_data")
    suspend fun getCount(): Int

}
