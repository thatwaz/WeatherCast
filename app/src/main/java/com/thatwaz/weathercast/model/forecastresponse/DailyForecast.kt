package com.thatwaz.weathercast.model.forecastresponse

import com.google.gson.annotations.SerializedName

data class DailyForecast(
    @SerializedName("date")
    val date: String,
    @SerializedName("highTemperature")
    val highTemperature: Double,
    @SerializedName("lowTemperature")
    val lowTemperature: Double,
    @SerializedName("weatherDescription")
    val weatherDescription: String,
    @SerializedName("weatherIcon")
    val weatherIcon: String,
    @SerializedName("chanceOfRain")
    val chanceOfRain: Double,
    @SerializedName("city")
    val cityName: String,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("feelsLikeTemperature")
    val feelsLikeTemperature: Double,
    @SerializedName("windSpeed")
    val windSpeed: Double,
    @SerializedName("windDeg")
    val windDeg: Int
)



