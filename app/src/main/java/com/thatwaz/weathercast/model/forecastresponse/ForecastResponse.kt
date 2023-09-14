package com.thatwaz.weathercast.model.forecastresponse

import com.google.gson.annotations.SerializedName


data class ForecastResponse(
    @SerializedName("cod")
    val cod: String,
    @SerializedName("message")
    val message: Int,
    @SerializedName("cnt")
    val cnt: Int,
    @SerializedName("list")
    val list: List<WeatherItem>,
    @SerializedName("city")
    val city: CityForecast
)

data class WeatherItem(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("main")
    val main: MainWeatherData,
    @SerializedName("weather")
    val weather: List<WeatherForecast>,
    @SerializedName("clouds")
    val clouds: CloudsForecast,
    @SerializedName("wind")
    val wind: WindForecast,
//    @SerializedName("visibility")
//    val visibility: Int,
//    @SerializedName("pop")
//    val pop: Double,
    @SerializedName("rain")
    val rain: RainForecast?,
    @SerializedName("sys")
    val sys: SysForecast,
    @SerializedName("dt_txt")
    val dtTxt: String
)


data class MainWeatherData(
    @SerializedName("temp")
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("sea_level")
    val seaLevel: Int,
//    @SerializedName("grnd_level")
//    val grndLevel: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("temp_kf")
    val tempKf: Double
)

data class WeatherForecast(
    @SerializedName("id")
    val id: Int,
    @SerializedName("main")
    val main: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("icon")
    val icon: String
)

data class CloudsForecast(
    @SerializedName("all")
    val all: Int
)

data class WindForecast(
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("deg")
    val deg: Int,
    @SerializedName("gust")
    val gust: Double
)

data class RainForecast(
    @SerializedName("3h")
    val `3h`: Double
)

data class SysForecast(
    @SerializedName("pod")
    val pod: String
)

data class CityForecast(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("coord")
    val coord: CoordForecast,
    @SerializedName("timezone")
    val timezone: Int,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long
)

data class CoordForecast(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double
)

