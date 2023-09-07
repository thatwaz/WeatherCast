package com.thatwaz.weathercast.model.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_weather_data")
data class HourlyWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val hourlyWeatherJson: String
)

