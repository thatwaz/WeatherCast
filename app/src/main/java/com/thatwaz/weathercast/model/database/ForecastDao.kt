package com.thatwaz.weathercast.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ForecastDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)

    @Query("SELECT * FROM forecast_data WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun getForecast(latitude: Double, longitude: Double): ForecastEntity

    @Query("DELETE FROM forecast_data WHERE id = (SELECT MIN(id) FROM forecast_data)")
    suspend fun deleteOldestEntry()

    @Query("SELECT COUNT(*) FROM forecast_data")
    suspend fun getCount(): Int
}
