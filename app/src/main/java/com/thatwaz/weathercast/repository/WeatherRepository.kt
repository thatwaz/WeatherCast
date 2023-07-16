package com.thatwaz.weathercast.repository

import com.thatwaz.weathercast.config.ApiConfig
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
        return weatherService.getWeatherData(appId, latitude, longitude)
    }

//    suspend fun getCityData(
//        appId: String,
//        latitude: Double,
//        longitude: Double
//    ): Response<City> {
//        return weatherService.getWeatherData(appId, latitude, longitude)
//    }

}


//class WeatherRepository {
//    private val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//
//    private val apiService = retrofit.create(ApiService::class.java)
//
//    suspend fun getWeatherData(apiConfig: String,latitude: Double, longitude: Double): Response<WeatherResponse> {
//        return apiService.getWeatherData(latitude, longitude)
//
//
//    }
//}
