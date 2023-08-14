package com.thatwaz.weathercast.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.database.WeatherDataEntity
import com.thatwaz.weathercast.model.database.WeatherDatabase
import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.repository.WeatherRepository
import com.thatwaz.weathercast.utils.ConversionUtil.convertRainToPercentage
import com.thatwaz.weathercast.utils.error.Resource
import retrofit2.Response
import javax.inject.Inject


class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val weatherDatabase: WeatherDatabase
) : ViewModel() {
    

    private val _weatherData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherData: LiveData<Resource<WeatherResponse>> get() = _weatherData

    private val _hourlyData = MutableLiveData<Resource<ForecastResponse>>()
    val hourlyData: LiveData<Resource<ForecastResponse>> get() = _hourlyData

//    private val _forecastData = MutableLiveData<Resource<ForecastResponse>>()
//    val forecastData: LiveData<Resource<ForecastResponse>> get() = _forecastData

    private val _forecastData = MutableLiveData<Resource<List<DailyForecast>>>()
    val forecastData: LiveData<Resource<List<DailyForecast>>> get() = _forecastData

    private fun consolidateForecastData(forecastResponse: ForecastResponse): List<DailyForecast> {
        val dailyForecasts = mutableListOf<DailyForecast>()

        // Group the forecast items by day
        val groupedForecasts = forecastResponse.list.groupBy { forecastItem ->
            forecastItem.dtTxt.substringBefore(" ") // Extract the date part
        }

        // Calculate high and low temperatures, weather description, and other properties for each day
        for ((date, forecasts) in groupedForecasts) {
            val highTemp = forecasts.maxByOrNull { it.main.tempMax }?.main?.tempMax ?: 0.0
            val lowTemp = forecasts.minByOrNull { it.main.tempMin }?.main?.tempMin ?: 0.0
            val weatherDescription = forecasts.firstOrNull()?.weather?.getOrNull(0)?.description ?: ""
            val weatherIcon = forecasts.firstOrNull()?.weather?.getOrNull(0)?.icon ?: ""
            val rainForecast = forecasts.firstOrNull()?.rain
            val chanceOfRain = convertRainToPercentage(rainForecast)
            val humidity = forecasts.firstOrNull()?.main?.humidity ?: 0
            val feelsLikeTemperature = forecasts.firstOrNull()?.main?.feelsLike ?: 0.0
            val windSpeed = forecasts.firstOrNull()?.wind?.speed ?: 0.0
            val windDeg = forecasts.firstOrNull()?.wind?.deg ?: 0

            dailyForecasts.add(
                DailyForecast(
                    date = date,
                    highTemperature = highTemp,
                    lowTemperature = lowTemp,
                    weatherDescription = weatherDescription,
                    weatherIcon = weatherIcon,
                    chanceOfRain = chanceOfRain.toDouble(),
                    humidity = humidity,
                    feelsLikeTemperature = feelsLikeTemperature,
                    windSpeed = windSpeed,
                    windDeg = windDeg
                )
            )
        }

        return dailyForecasts
    }



//    private fun consolidateForecastData(forecastResponse: ForecastResponse): List<DailyForecast> {
//        Log.i("MOH!", "consolidateForecastData called")
//        val dailyForecasts = mutableListOf<DailyForecast>()
//
//        // Group the forecast items by day
//        val groupedForecasts = forecastResponse.list.groupBy { forecastItem ->
//            forecastItem.dtTxt.substringBefore(" ") // Extract the date part
//        }
//
//        // Calculate high and low temperatures for each day
//        for ((date, forecasts) in groupedForecasts) {
//            val highTemp = forecasts.maxByOrNull { it.main.tempMax }?.main?.tempMax ?: 0.0
//            val lowTemp = forecasts.minByOrNull { it.main.tempMin }?.main?.tempMin ?: 0.0
//            val weatherDescription = forecasts.firstOrNull()?.weather?.getOrNull(0)?.description ?: ""
//
//            dailyForecasts.add(
//                DailyForecast(
//                    date = date,
//                    highTemperature = highTemp,
//                    lowTemperature = lowTemp,
//                    weatherDescription = weatherDescription
//                )
//            )
//        }
//
//        return dailyForecasts
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

    suspend fun fetchHourlyData(latitude: Double, longitude: Double) {

        _hourlyData.value = Resource.Loading()

        try {
            val hourlyResponse = repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)
            if (hourlyResponse.isSuccessful) {
                val hourlyResponseBody = hourlyResponse.body()
                if (hourlyResponseBody != null) {
                    _hourlyData.value = Resource.Success(hourlyResponseBody)
                } else {
                    _hourlyData.value = Resource.Error("Null response body")
                }
            } else {
                _hourlyData.value =
                    Resource.Error("Error fetching hourly data: ${hourlyResponse.code()}")
            }
        } catch (e: Exception) {
            handleError("Error fetching hourly data: ${e.message}")
            _hourlyData.value = Resource.Error("Error fetching hourly data: ${e.message}")
        }
    }

    suspend fun fetchForecastData(latitude: Double, longitude: Double) {
        // Use the same _forecastData LiveData instance
        _forecastData.value = Resource.Loading()

        try {
            val forecastResponse = repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)
            if (forecastResponse.isSuccessful) {
                val forecastResponseBody = forecastResponse.body()
                if (forecastResponseBody != null) {
                    val consolidatedData = consolidateForecastData(forecastResponseBody)
                    _forecastData.value = Resource.Success(consolidatedData)
                    Log.i("MOH!", "Consol is $consolidatedData")
                } else {
                    _forecastData.value = Resource.Error("Null response body")
                }
            } else {
                _forecastData.value = Resource.Error("Error fetching forecast data: ${forecastResponse.code()}")
            }
        } catch (e: Exception) {
            _forecastData.value = Resource.Error("Error fetching forecast data: ${e.message}")
        }
    }


//    suspend fun fetchForecastData(latitude: Double, longitude: Double) : LiveData<Resource<List<DailyForecast>>> {
//        // Define a LiveData for consolidated forecast data
//        val consolidatedDataLiveData = MutableLiveData<Resource<List<DailyForecast>>>()
//
//        consolidatedDataLiveData.value = Resource.Loading()
//
//        try {
//            val forecastResponse = repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)
//            if (forecastResponse.isSuccessful) {
//                val forecastResponseBody = forecastResponse.body()
//
//                if (forecastResponseBody != null) {
//                    val consolidatedData = consolidateForecastData(forecastResponseBody)
//                    consolidatedDataLiveData.value = Resource.Success(consolidatedData)
//                    Log.i("MOH!","Consol is $consolidatedData")
//                } else {
//                    consolidatedDataLiveData.value = Resource.Error("Null response body")
//                }
//            } else {
//                consolidatedDataLiveData.value =
//                    Resource.Error("Error fetching forecast data: ${forecastResponse.code()}")
//            }
//        } catch (e: Exception) {
//            consolidatedDataLiveData.value = Resource.Error("Error fetching forecast data: ${e.message}")
//        }
//
//        return consolidatedDataLiveData
//    }

}



