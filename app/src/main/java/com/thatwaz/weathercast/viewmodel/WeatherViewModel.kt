package com.thatwaz.weathercast.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.database.WeatherDataEntity
import com.thatwaz.weathercast.model.database.WeatherDatabase
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse

import com.thatwaz.weathercast.repository.WeatherRepository


class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _forecastData = MutableLiveData<ForecastResponse>()
    val forecastData: LiveData<ForecastResponse> get() = _forecastData

    private val weatherDatabase: WeatherDatabase by lazy {
        WeatherDatabase.getInstance(WeatherCastApplication.instance)
    }

    suspend fun fetchWeatherData(latitude: Double, longitude: Double) {
        try {
            // Check if cached data exists in the Room database
            val cachedWeatherData = weatherDatabase.weatherDataDao().getWeatherData(latitude, longitude)
            if (cachedWeatherData != null) {
                // Cached data found, use it directly
                _weatherData.value = Gson().fromJson(cachedWeatherData.weatherJson, WeatherResponse::class.java)
            } else {
                // No cached data found, fetch data from the API
                val weatherResponse = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
                if (weatherResponse.isSuccessful) {
                    Log.i("DOH!","Current Weather Response is ${_weatherData.value}")
                    _weatherData.value = weatherResponse.body()

                    // Cache the API response in the Room database
                    _weatherData.value?.let {
                        val weatherDataEntity = WeatherDataEntity(
                            latitude = latitude,
                            longitude = longitude,
                            weatherJson = Gson().toJson(it)
                        )
                        weatherDatabase.weatherDataDao().insertWeatherData(weatherDataEntity)
                    }
                } else {
                    // Handle current weather data error
                    Log.e("WeatherViewModel", "Error fetching weather data: ${weatherResponse.code()}")
                }
            }
        } catch (e: Exception) {
            // Handle any other exceptions that might occur during the network request
            Log.e("WeatherViewModel", "Error fetching data: ${e.message}")
        }
    }
}





//class WeatherViewModel : ViewModel() {
//
//    private val repository = WeatherRepository()
//    private val _weatherData = MutableLiveData<WeatherResponse>()
//    val weatherData: MutableLiveData<WeatherResponse> = _weatherData
//
//    private val _forecastData = MutableLiveData<ForecastResponse>()
//    val forecastData: LiveData<ForecastResponse> get() = _forecastData
//
//
//    private val _sunriseTime = MutableLiveData<Int>()
//    val sunriseTime: MutableLiveData<Int> get() = _sunriseTime
//
//    fun fetchWeatherData(latitude: Double, longitude: Double) {
//        try {
//
//            viewModelScope.launch {
//                val response = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
//                if (response.isSuccessful) {
//                    _weatherData.value = response.body()
//
//                    val weatherResponse = response.body()
//                    val kelvinTemp = weatherResponse?.main?.temp
//                    val fahrenheitTemp = kelvinTemp?.let { (it - 273.15) * 9 / 5 + 32 }
//                    Log.i("DOH!", "OUTPUT IS ${_weatherData.value}")
//
//
//                } else {
//                    // Handle error
//                    Log.e("WeatherViewModel", "Error fetching weather data: ${response.code()}")
//                }
//
//            }
//        } catch (e: Exception) {
//            // Handle any other exceptions that might occur during the network request
//            Log.e("WeatherViewModel", "Error fetching weather data: ${e.message}")
//            // Handle the error in a similar way as shown in the else block above
//        }
//    }
//}


