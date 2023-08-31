package com.thatwaz.weathercast.viewmodel



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.database.ForecastEntity
import com.thatwaz.weathercast.model.database.HourlyWeatherEntity
import com.thatwaz.weathercast.model.database.WeatherDataEntity
import com.thatwaz.weathercast.model.database.WeatherDatabase
import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
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

    //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    suspend fun logWeatherDataCount() {
        val count = weatherDatabase.weatherDataDao().getCount()
        Log.i("MOH!", "Total number of entries: $count")
    }




    private val _weatherData = MutableLiveData<Resource<WeatherResponse>>()
    val weatherData: LiveData<Resource<WeatherResponse>> get() = _weatherData

    private val _hourlyData = MutableLiveData<Resource<ForecastResponse>>()
    val hourlyData: LiveData<Resource<ForecastResponse>> get() = _hourlyData


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
            val weatherDescription =
                forecasts.firstOrNull()?.weather?.getOrNull(0)?.description ?: ""
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
                    windDeg = windDeg,
                    cityName = forecastResponse.city.name
                )
            )
        }
        return dailyForecasts
    }

    suspend fun fetchWeatherData(latitude: Double, longitude: Double) {

        //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
        logWeatherDataCount()
        _weatherData.value = Resource.Loading()


        // FOR initial database entry removal
//        val initialCount = weatherDatabase.weatherDataDao().getCount()
//        Log.i("MOH!", "Before: Total number of entries: $initialCount")
//
//        var currentCount = initialCount
//
//        while (currentCount > 10) {
//            weatherDatabase.weatherDataDao().deleteOldestEntry()
//            currentCount = weatherDatabase.weatherDataDao().getCount()
//        }
//
//        Log.i("MOH!", "After: Total number of entries: $currentCount")

        val count = weatherDatabase.weatherDataDao().getCount()
        Log.i("MOH!", "Before: Total number of entries: $count")
        if (count > 10) {
            weatherDatabase.weatherDataDao().deleteOldestEntry()
        }
        val newCount = weatherDatabase.weatherDataDao().getCount()
        Log.i("MOH!", "After: Total number of entries: $newCount")

        val fetchBlock: suspend () -> Response<WeatherResponse> = {
            repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
        }

        val cacheBlock: suspend (WeatherResponse) -> Unit = { weatherResponseBody ->
            val weatherDataEntity = WeatherDataEntity(
                latitude = latitude,
                longitude = longitude,
                weatherJson = Gson().toJson(weatherResponseBody)
            )
            weatherDatabase.weatherDataDao().insertWeatherData(weatherDataEntity)
        }

        _weatherData.value = fetchAndCacheData(fetchBlock, cacheBlock)
    }

    suspend fun fetchHourlyData(latitude: Double, longitude: Double) {
        _hourlyData.value = Resource.Loading()

        val fetchBlock: suspend () -> Response<ForecastResponse> = {
            repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)
        }

        val cacheBlock: suspend (ForecastResponse) -> Unit = { hourlyResponseBody ->
            val hourlyWeatherEntity = HourlyWeatherEntity(
                latitude = latitude,
                longitude = longitude,
                hourlyWeatherJson = Gson().toJson(hourlyResponseBody)
            )
            weatherDatabase.hourlyWeatherDao().insertHourlyWeather(hourlyWeatherEntity)
        }

        _hourlyData.value = fetchAndCacheData(fetchBlock, cacheBlock)
    }

    suspend fun fetchForecastData(latitude: Double, longitude: Double) {
        _forecastData.value = Resource.Loading()

        val fetchBlock: suspend () -> Response<ForecastResponse> = {
            repository.getForecastData(ApiConfig.APP_ID, latitude, longitude)
        }

        val cacheBlock: suspend (ForecastResponse) -> Unit = { forecastResponseBody ->
            val forecastEntity = ForecastEntity(
                latitude = latitude,
                longitude = longitude,
                forecastJson = Gson().toJson(forecastResponseBody)
            )
            weatherDatabase.forecastDao().insertForecast(forecastEntity)
        }

        _forecastData.value = fetchAndCacheForecastData(fetchBlock, cacheBlock)
    }




    private suspend fun <T> fetchAndCacheData(
        fetchBlock: suspend () -> Response<T>,
        cacheBlock: suspend (T) -> Unit
    ): Resource<T> {
        try {
            val response = fetchBlock()
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    cacheBlock(responseBody)
                    Log.i("DOH!","Response is $responseBody")
                    Resource.Success(responseBody)

                } else {
                    Resource.Error("Null response body")
                }
            } else {
                Resource.Error("Error fetching data: ${response.code()}")
            }
        } catch (e: Exception) {
            return Resource.Error("Error fetching data: ${e.message}")
        }
    }

    private suspend fun fetchAndCacheForecastData(
        fetchBlock: suspend () -> Response<ForecastResponse>,
        cacheBlock: suspend (ForecastResponse) -> Unit
    ): Resource<List<DailyForecast>> {
        try {
            val response = fetchBlock()
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    cacheBlock(responseBody)
                    val consolidatedData = consolidateForecastData(responseBody)
                    Resource.Success(consolidatedData)
                } else {
                    Resource.Error("Null response body")
                }
            } else {
                Resource.Error("Error fetching data: ${response.code()}")
            }
        } catch (e: Exception) {
            return Resource.Error("Error fetching data: ${e.message}")
        }
    }
}



