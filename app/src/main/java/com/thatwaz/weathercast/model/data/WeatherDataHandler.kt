package com.thatwaz.weathercast.model.data

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.thatwaz.weathercast.utils.NetworkUtil
import com.thatwaz.weathercast.utils.NetworkUtil.isNetworkConnected
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class WeatherDataHandler @Inject constructor(
    private val applicationContext: Context,
    private val viewModel: WeatherViewModel
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
    private val locationRepository = LocationRepository(fusedLocationClient)
    private val connectivityManager =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun requestLocationData() {
        locationRepository.getCurrentLocation { lat, lon ->
            getLocationWeatherDetails(lat, lon)
        }
    }


    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        Log.d("WeatherDataHandler", "fetchWeatherData - Start")
        val start = System.currentTimeMillis()
        val isConnected = isNetworkConnected(connectivityManager) && NetworkUtil.isInternetAvailable(connectivityManager)

        if (isConnected) {
            viewModel.viewModelScope.launch {
                viewModel.fetchWeatherData(latitude, longitude)
                val end = System.currentTimeMillis()
                Log.d("WeatherDataHandler", "Weather data fetched in ${end - start} ms")
            }
        } else {
            Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        fetchWeatherData(latitude, longitude)
    }

    fun fetchWeatherForecast(forecastType: ForecastType) {
        val isConnected = isNetworkConnected(connectivityManager) && NetworkUtil.isInternetAvailable(connectivityManager)

        if (isConnected) {
            locationRepository.getCurrentLocation { lat, lon ->
                viewModel.viewModelScope.launch {
                    when (forecastType) {
                        ForecastType.HOURLY -> viewModel.fetchHourlyData(lat, lon)
                        ForecastType.DAILY -> viewModel.fetchForecastData(lat, lon)
                    }
                }
            }
        } else {
            Toast.makeText(applicationContext, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    enum class ForecastType {
        HOURLY, DAILY
    }

    fun cleanUp() {
        Log.i("MOH!","Data handler loc cleaned up")
        locationRepository.removeLocationUpdates()
    }
}






