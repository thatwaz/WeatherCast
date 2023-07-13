package com.thatwaz.weathercast.model.weatherresponse


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val temp: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val city: City,
    val dt: Int,
    val sys: Sys,
    val id: Int,
    val name: String,
    val cod: Int
) : Serializable
//data class WeatherResponse(
//    @SerializedName("coord")
//    val coord: Coord,
//    @SerializedName("city")
//    val city: City,
//    @SerializedName("cnt")
//    val cnt: Int,
//    @SerializedName("cod")
//    val cod: String,
//    @SerializedName("list")
//    val list: List<Weather>,
//    @SerializedName("message")
//    val message: Int,
//    ) : List<WeatherResponse>