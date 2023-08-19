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
    @SerializedName("cityName")
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


//data class DailyForecast(
//    val date: String,
//    val highTemperature: Double,
//    val lowTemperature: Double,
//    val weatherDescription: String,
//    val weatherIcon: String,
//    val chanceOfRain: Double,
//    val cityName: String,
//    val humidity: Int,
//    val feelsLikeTemperature: Double,
//    val windSpeed: Double,
//    val windDeg: Int,
//    )

