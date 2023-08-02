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
import com.thatwaz.weathercast.utils.error.Resource
import javax.inject.Inject


class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val weatherDatabase: WeatherDatabase
) : ViewModel() {

//    private val repository = WeatherRepository()

//    private val _weatherData = MutableLiveData<WeatherResponse>()
//    val weatherData: LiveData<WeatherResponse> get() = _weatherData

    private val _weatherData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherData: LiveData<Resource<WeatherResponse>> get() = _weatherData

    private val _forecastData = MutableLiveData<Resource<ForecastResponse>>()
    val forecastData: LiveData<Resource<ForecastResponse>> get() = _forecastData


//    private val weatherDatabase: WeatherDatabase by lazy {
//        WeatherDatabase.getInstance(WeatherCastApplication.instance)
//    }

    private fun handleError(errorMsg: String) {
        _weatherData.value = Resource.Error(errorMsg)
    }


    suspend fun fetchWeatherData(latitude: Double, longitude: Double) {
        _weatherData.value = Resource.Loading()

        try {
            // Check if cached data exists in the Room database
            val cachedWeatherData =
                weatherDatabase.weatherDataDao().getWeatherData(latitude, longitude)
            if (cachedWeatherData != null) {
                // Cached data found, use it directly
                _weatherData.value = Resource.Success(
                    Gson().fromJson(
                        cachedWeatherData.weatherJson,
                        WeatherResponse::class.java
                    )
                )
            } else {
                // No cached data found, fetch data from the API
                val weatherResponse =
                    repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
                if (weatherResponse.isSuccessful) {
                    val weatherResponseBody = weatherResponse.body()
                    if (weatherResponseBody != null) {
                        Log.i("DOH!", "Response: $weatherResponseBody")
                        _weatherData.value = Resource.Success(weatherResponseBody)

                        // Cache the API response in the Room database
                        val weatherDataEntity = WeatherDataEntity(
                            latitude = latitude,
                            longitude = longitude,
                            weatherJson = Gson().toJson(weatherResponseBody)
                        )
                        weatherDatabase.weatherDataDao().insertWeatherData(weatherDataEntity)
                    } else {
                        // Handle null response body here if needed
                        _weatherData.value = Resource.Error("Null response body")
                    }
                } else {
                    // Handle current weather data error
                    _weatherData.value =
                        Resource.Error("Error fetching weather data: ${weatherResponse.code()}")
                }
            }
        } catch (e: Exception) {
            handleError("Error fetching data: ${e.message}")
            // Handle any other exceptions that might occur during the network request
            _weatherData.value = Resource.Error("Error fetching data: ${e.message}")
        }
    }

    suspend fun fetchForecastData(latitude: Double, longitude: Double) {
        Log.i("MOH!", "fetchForecastData is called with latitude=$latitude, longitude=$longitude")

        _forecastData.value = Resource.Loading()

        try {
            // Fetch forecast data from the API using the repository's getForecastData function
            val forecastResponse = repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)

            if (forecastResponse.isSuccessful) {
                val forecastResponseBody = forecastResponse.body()
                if (forecastResponseBody != null) {
                    // Forecast data fetched successfully
                    _forecastData.value = Resource.Success(forecastResponseBody)
                    Log.i("MOH!", "Forecast Response: $forecastResponseBody")

                    // If needed, you can handle the forecast data here or pass it to the fragment
                } else {
                    // Handle null response body here if needed
                    _forecastData.value = Resource.Error("Null response body")
                }
            } else {
                // Handle forecast data error
                _forecastData.value = Resource.Error("Error fetching forecast data: ${forecastResponse.code()}")
            }
        } catch (e: Exception) {
            // Handle any other exceptions that might occur during the network request
            handleError("Error fetching forecast data: ${e.message}")
            _forecastData.value = Resource.Error("Error fetching forecast data: ${e.message}")
        }
    }


//    suspend fun fetchForecastData(latitude: Double, longitude: Double) {
//        _forecastData.value = Resource.Loading()
//
//        try {
//            // Fetch forecast data from the API using the repository's getForecastData function
//            val forecastResponse =
//                repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)
//
//            if (forecastResponse.isSuccessful) {
//                val forecastResponseBody = forecastResponse.body()
//                if (forecastResponseBody != null) {
//                    // Forecast data fetched successfully
//                    _forecastData.value = Resource.Success(forecastResponseBody)
//                    Log.i("DOH!", "Forecast Response: $forecastResponseBody")
//
//                    // If needed, you can handle the forecast data here or pass it to the fragment
//                } else {
//                    // Handle null response body here if needed
//                    _forecastData.value = Resource.Error("Null response body")
//                }
//            } else {
//                // Handle forecast data error
//                _forecastData.value =
//                    Resource.Error("Error fetching forecast data: ${forecastResponse.code()}")
//            }
//        } catch (e: Exception) {
//            // Handle any other exceptions that might occur during the network request
//            handleError("Error fetching forecast data: ${e.message}")
//            _forecastData.value = Resource.Error("Error fetching forecast data: ${e.message}")
//        }
//    }

}



