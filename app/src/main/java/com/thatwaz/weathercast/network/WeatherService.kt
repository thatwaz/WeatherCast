package com.thatwaz.weathercast.network

import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {

    @GET("2.5/weather")
    suspend fun getWeatherData(
        @Query("appId") appId: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        ): Response<WeatherResponse>

    @GET("2.5/forecast")
    suspend fun getForecastData(
        @Query("appId") appId: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Response<ForecastResponse>

}


