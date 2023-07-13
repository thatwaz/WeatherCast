package com.thatwaz.weathercast.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.weatherresponse.City
import com.thatwaz.weathercast.model.weatherresponse.Weather
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.repository.WeatherRepository
import kotlinx.coroutines.launch



class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: MutableLiveData<WeatherResponse> = _weatherData





    fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val response = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
            if (response.isSuccessful) {
                _weatherData.value = response.body()
                val weatherResponse = response.body()
                val kelvinTemp = weatherResponse?.main?.temp
                val fahrenheitTemp = kelvinTemp?.let { (it - 273.15) * 9/5 + 32 }
                Log.i("DOH!", "OUTPUT IS ${_weatherData.value}")
                Log.i("DOH!", "name is ${weatherResponse?.name}")
                Log.i("DOH!", "Temp is ${fahrenheitTemp!!.toInt()}")
            } else {
                // Handle error
            }
        }
    }


}



//class WeatherViewModel : ViewModel() {
//
//    private val repository = WeatherRepository()
//
//
//
//    private val _weatherData = MutableLiveData<List<WeatherResponse>>()
//    val weatherData: MutableLiveData<List<WeatherResponse>> get() = _weatherData
//
//
//    private val _city = MutableLiveData<City>()
//    val city: LiveData<City> = _city
//
////
////    init {
////        _weatherData.value = emptyList() // Initialize the LiveData with an empty list
////    }
//
//    fun fetchWeatherData(latitude: Double, longitude: Double) {
//        viewModelScope.launch {
//            val response = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
//            if (response.isSuccessful) {
//                _weatherData.value = response.body()
//                Log.i("DOH!","OUTPUT IS ${_weatherData.value}")
//                Log.i("DOH!","city IS ${_city.value}")
//            } else {
//                // Handle error
//            }
//        }
//    }
//}







//class WeatherViewModel : ViewModel() {
//
//    private val repository = WeatherRepository()
//
//    private val _weatherData = MutableLiveData<List<Weather>>()
//    val weatherData: LiveData<List<Weather>> get() = _weatherData
//
//    fun fetchWeatherData(latitude: Double, longitude: Double) {
//        viewModelScope.launch {
//            val response = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
//            if (response.isSuccessful) {
//                _weatherData.value = response.body()?.list
//                Log.i("DOH!","Output is ${_weatherData.value}")
//
//            } else {
//                Log.i("DOH!","ERRRRROR!!!")
//                // Handle error
//            }
//        }
//    }
//}



//class WeatherViewModel : ViewModel() {
//
//    private val repository = WeatherRepository()
//
//    private val _weatherData = MutableLiveData<WeatherResponse>()
//    val weatherData: LiveData<WeatherResponse> get() = _weatherData
//
//    fun fetchWeatherData(latitude: Double, longitude: Double) {
//        viewModelScope.launch {
//            val response = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
//            if (response.isSuccessful) {
//                _weatherData.value = response.body()
//
//            } else {
//                // Handle error
//            }
//        }
//    }
//}






//class WeatherViewModel() : ViewModel() {
//
//    private val repository = WeatherRepository()
//
//    private val _weatherData = MutableLiveData<WeatherResponse>()
//    val weatherData: LiveData<WeatherResponse> get() = _weatherData
//
//    fun fetchWeatherData(latitude: Double, longitude: Double) {
//        viewModelScope.launch {
//            val response = repository.getWeatherData(latitude, longitude)
//            if (response.isSuccessful) {
//                _weatherData.value = response.body()
//                Log.i("DOH!","${_weatherData.value}")
//            } else {
//                // Handle error
//            }
//        }
//    }
//
//}