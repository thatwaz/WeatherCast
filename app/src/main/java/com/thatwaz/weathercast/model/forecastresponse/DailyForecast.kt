package com.thatwaz.weathercast.model.forecastresponse

data class DailyForecast(
    val date: String,
    val highTemperature: Double,
    val lowTemperature: Double,
    val weatherDescription: String,
    // Add any other relevant properties
)
