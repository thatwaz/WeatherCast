package com.thatwaz.weathercast.model.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.thatwaz.weathercast.utils.NetworkUtil
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch


class WeatherDataHandler(
    private val context: Context,
    private val viewModel: WeatherViewModel
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRepository = LocationRepository(fusedLocationClient)
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun requestLocationData() {
        locationRepository.getCurrentLocation { lat, lon ->
            getLocationWeatherDetails(lat, lon)
        }
    }

    private fun isNetworkConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    private fun fetchWeatherData(latitude: Double, longitude: Double) {
        val isConnected = isNetworkConnected() && NetworkUtil.isInternetAvailable(connectivityManager)

        if (isConnected) {
            viewModel.viewModelScope.launch {
                viewModel.fetchWeatherData(latitude, longitude)
            }
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        fetchWeatherData(latitude, longitude)
    }

    fun fetchWeatherForecast(latitude: Double, longitude: Double, forecastType: ForecastType) {
        val isConnected = isNetworkConnected() && NetworkUtil.isInternetAvailable(connectivityManager)

        if (isConnected) {
            viewModel.viewModelScope.launch {
                when (forecastType) {
                    ForecastType.HOURLY -> viewModel.fetchHourlyData(latitude, longitude)
                    ForecastType.DAILY -> viewModel.fetchForecastData(latitude, longitude)
                }
            }
        } else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    enum class ForecastType {
        HOURLY, DAILY
    }
}




