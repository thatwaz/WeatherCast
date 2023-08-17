package com.thatwaz.weathercast.model.forecastresponse

import com.google.gson.annotations.SerializedName

data class DailyForecast(
    val date: String,
    val highTemperature: Double,
    val lowTemperature: Double,
    val weatherDescription: String,
    val weatherIcon: String,
    val chanceOfRain: Double,
    val cityName: String,
    val humidity: Int,
    val feelsLikeTemperature: Double,
    val windSpeed: Double,
    val windDeg: Int,
    )

