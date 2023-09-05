package com.thatwaz.weathercast.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.utils.error.Resource
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherData(weatherData: WeatherDataEntity)

//    @Query("SELECT * FROM weather_data WHERE latitude = :latitude AND longitude = :longitude")
//    fun getWeatherData(latitude: Double, longitude: Double): Flow<Resource<WeatherResponse>>


    @Query("SELECT * FROM weather_data WHERE latitude = :latitude AND longitude = :longitude")
    suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherDataEntity?

    @Query("SELECT COUNT(*) FROM weather_data")
    suspend fun getCount(): Int

    @Query("DELETE FROM weather_data WHERE id = (SELECT MIN(id) FROM weather_data)")
    suspend fun deleteOldestEntry()

}
