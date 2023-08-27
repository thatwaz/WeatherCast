package com.thatwaz.weathercast.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_data")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val forecastJson: String
)

