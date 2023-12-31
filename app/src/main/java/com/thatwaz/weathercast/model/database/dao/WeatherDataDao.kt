package com.thatwaz.weathercast.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thatwaz.weathercast.model.database.entities.WeatherDataEntity

@Dao
interface WeatherDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataEntity)

    @Query("SELECT * FROM weather_data WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherDataEntity?

    @Query("SELECT COUNT(*) FROM weather_data")
    suspend fun getCount(): Int

    @Query("DELETE FROM weather_data WHERE id = (SELECT MIN(id) FROM weather_data)")
    suspend fun deleteOldestEntry()

    @Query("DELETE FROM weather_data")
    suspend fun deleteAllCurrentWeatherEntries()

}
