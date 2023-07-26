package com.thatwaz.weathercast.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse

import com.thatwaz.weathercast.repository.WeatherRepository
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _forecastData = MutableLiveData<ForecastResponse>()
    val forecastData: LiveData<ForecastResponse> get() = _forecastData



    fun fetchWeatherData(latitude: Double, longitude: Double) {
        try {
            viewModelScope.launch {
                val weatherResponse = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
                if (weatherResponse.isSuccessful) {
                    _weatherData.value = weatherResponse.body()
                    // You can update other properties based on the weatherResponse if needed
                    Log.i("DOH!","Current Weather Response is ${_weatherData.value}")

                    val forecastResponse = repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)
                    if (forecastResponse.isSuccessful) {
                        _forecastData.value = forecastResponse.body()
                        Log.i("DOH!","Forecast is ${_forecastData.value}")
                        // Handle forecast data as needed
                    } else {
                        // Handle forecast data error
                        Log.e("WeatherViewModel", "Error fetching forecast data: ${forecastResponse.code()}")
                    }

                } else {
                    // Handle current weather data error
                    Log.e("WeatherViewModel", "Error fetching weather data: ${weatherResponse.code()}")
                }
            }
        } catch (e: Exception) {
            // Handle any other exceptions that might occur during the network request
            Log.e("WeatherViewModel", "Error fetching data: ${e.message}")
            // Handle the error in a similar way as shown in the else blocks above
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


