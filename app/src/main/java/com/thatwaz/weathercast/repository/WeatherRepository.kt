package com.thatwaz.weathercast.repository

import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.network.WeatherService
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {

    private val weatherService: WeatherService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherService = retrofit.create(WeatherService::class.java)
    }

    suspend fun getWeatherData(
        appId: String,
        latitude: Double,
        longitude: Double,
    ): Response<WeatherResponse> {
        return weatherService.getWeatherData(appId, latitude,longitude)
    }

    suspend fun getForecastData(
        appId: String,
        latitude: Double,
        longitude: Double
    ): Response<ForecastResponse> {
        return weatherService.getForecastData(appId,latitude,longitude)
    }


}


