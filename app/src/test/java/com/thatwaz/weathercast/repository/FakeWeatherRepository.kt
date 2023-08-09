package com.thatwaz.weathercast.repository

import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.weatherresponse.*
import com.thatwaz.weathercast.network.WeatherService
import retrofit2.Response

class FakeWeatherRepository : WeatherService {

    private val weatherList = listOf(
        Weather(
            description = "Sunny",
            icon = "2d",
            id = 54,
            main = "Hello"
        ))

    private val weather = mutableListOf<WeatherResponse>()


    init {

        weather.add(
            WeatherResponse(
                "cloudy",
                Clouds(2),
                4,
                Coord(21.55, 16.66),
                2,
                1,
                Main(2.22,2,2,396.0,400.0,425.0),
                "cloudy as hell",
                Sys("U.S.",1,222,333,555),
                222,
                333,
                weatherList,
                Wind(22,22.2)
            )
        )
    }

    override suspend fun getWeatherData(
        appId: String,
        latitude: Double,
        longitude: Double
    ): Response<WeatherResponse> {
        return Response.success(weather[0])
    }

    override suspend fun getForecastData(
        appId: String,
        latitude: Double,
        longitude: Double
    ): Response<ForecastResponse> {
        TODO("Not yet implemented")
    }
}