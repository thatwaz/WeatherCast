package com.thatwaz.weathercast.viewmodel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.thatwaz.weathercast.config.ApiConfig
import com.thatwaz.weathercast.model.database.entities.WeatherDataEntity
import com.thatwaz.weathercast.model.database.WeatherDatabase
import com.thatwaz.weathercast.model.database.dbcleanup.DatabaseCleanupUtil
import com.thatwaz.weathercast.model.database.entities.ForecastEntity
import com.thatwaz.weathercast.model.database.entities.HourlyWeatherEntity
import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
import com.thatwaz.weathercast.model.forecastresponse.ForecastResponse
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.repository.WeatherRepository
import com.thatwaz.weathercast.utils.ForecastDataConsolidator
import com.thatwaz.weathercast.utils.error.Resource
import kotlinx.coroutines.launch
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

    private val _forecastData = MutableLiveData<Resource<List<DailyForecast>>>()
    val forecastData: LiveData<Resource<List<DailyForecast>>> get() = _forecastData


    suspend fun fetchWeatherData(latitude: Double, longitude: Double) {
        _weatherData.value = Resource.Loading()

        DatabaseCleanupUtil.cleanupCurrentWeatherDatabase(weatherDatabase)

        val cachedData = weatherDatabase.weatherDataDao().getWeatherData(latitude, longitude)
        if (cachedData != null) {
            Log.i("WeatherApp", "Using cached data for weather.")
            val weatherResponse = Gson().fromJson(cachedData.weatherJson, WeatherResponse::class.java)


            _weatherData.postValue(Resource.Success(weatherResponse))
            return
        }

        val fetchBlock: suspend () -> Response<WeatherResponse> = {
            Log.i("WeatherApp", "Making a new API call for weather data.")
//            val response = repository.getWeatherData(ApiConfig.APP_ID, latitude, longitude)
//            Log.i("WeatherApp", "API Response: ${response.body()?.toString() ?: "No response body"}")
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

        // Use the general method to fetch and cache
        _weatherData.value = fetchAndCacheData(fetchBlock, cacheBlock)
    }

    fun refreshCurrentWeatherData() {
        viewModelScope.launch {
            weatherDatabase.weatherDataDao().deleteAllCurrentWeatherEntries()
        }
    }

    suspend fun fetchHourlyData(latitude: Double, longitude: Double) {
        _hourlyData.value = Resource.Loading()

        // Prune old database entries if needed.
        DatabaseCleanupUtil.cleanupHourlyWeatherDatabase(weatherDatabase)

        // Check for existing cached data
        val cachedData = weatherDatabase.hourlyWeatherDao().getHourlyWeather(latitude, longitude)
        if (cachedData != null) {
            Log.i("WeatherApp", "Using cached data for hourly weather.")
            val hourlyResponse = Gson().fromJson(cachedData.hourlyWeatherJson, ForecastResponse::class.java)
            _hourlyData.postValue(Resource.Success(hourlyResponse))
            return
        }

        // Prepare blocks for network fetch and cache operations
        val fetchBlock: suspend () -> Response<ForecastResponse> = {
            Log.i("WeatherApp", "Making a new API call for hourly weather data.")
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

        // Fetch and cache data
        _hourlyData.value = fetchAndCacheData(fetchBlock, cacheBlock)
    }

    fun refreshHourlyWeatherData() {
        viewModelScope.launch {
            weatherDatabase.hourlyWeatherDao().deleteAllHourlyWeatherEntries()
        }
    }

    suspend fun fetchForecastData(latitude: Double, longitude: Double) {
        _forecastData.value = Resource.Loading()

        // Prune old database entries if needed.
        DatabaseCleanupUtil.cleanupForecastWeatherDatabase(weatherDatabase)

        // Check for existing cached data
        val cachedData = weatherDatabase.forecastDao().getForecast(latitude, longitude)
        if (cachedData != null) {
            Log.i("WeatherApp", "Using cached data for daily weather.")
            val forecastResponse = Gson().fromJson(cachedData.forecastJson, ForecastResponse::class.java)
            val consolidatedData = ForecastDataConsolidator.consolidate(forecastResponse)
            _forecastData.postValue(Resource.Success(consolidatedData))
            return
        }

        // Prepare blocks for network fetch and cache operations
        val fetchBlock: suspend () -> Response<ForecastResponse> = {
            Log.i("WeatherApp", "Making a new API call for daily weather data.")
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

        // Fetch and cache data
        val fetchedData = fetchAndCacheForecastData(fetchBlock, cacheBlock)

        // Update LiveData
        _forecastData.value = fetchedData
    }

    fun refreshForecastWeatherData() {
        viewModelScope.launch {
            weatherDatabase.forecastDao().deleteAllForecastWeatherEntries()
        }
    }


    private suspend fun <T> fetchAndCacheData(
        fetchBlock: suspend () -> Response<T>,
        cacheBlock: suspend (T) -> Unit
    ): Resource<T> {
        try {
            Log.d("WeatherViewModel", "Attempting to fetch new data...")
            val response = fetchBlock()
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    cacheBlock(responseBody)
                    Log.d("WeatherViewModel", "Data successfully fetched and cached.")
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
            Log.d("WeatherViewModel", "Attempting to fetch new forecast data...")
            val response = fetchBlock()
            return if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    cacheBlock(responseBody)
                    Log.d("WeatherViewModel", "Forecast data successfully fetched and cached.")
                    val consolidatedData = ForecastDataConsolidator.consolidate(responseBody)
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

