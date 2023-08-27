package com.thatwaz.weathercast.model.database

import androidx.room.*

@Dao
interface HourlyWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourlyWeather: HourlyWeatherEntity)

    @Query("SELECT * FROM hourly_weather_data WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun getHourlyWeather(latitude: Double, longitude: Double): HourlyWeatherEntity?


}
