package com.thatwaz.weathercast.model.data

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices

import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.utils.NetworkUtil
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

class WeatherDataHandler(
    private val context: Context,
    private val viewModel: WeatherViewModel
) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRepository = LocationRepository(fusedLocationClient)

    fun requestLocationData(latitude: Double, longitude: Double) {
        locationRepository.getCurrentLocation { lat, lon ->
            getLocationWeatherDetails(lat, lon)
        }
    }

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected =
            networkInfo != null && networkInfo.isConnected && NetworkUtil.isInternetAvailable(
                connectivityManager
            )

        if (isConnected) {
            viewModel.viewModelScope.launch {
                viewModel.fetchWeatherData(latitude, longitude)
            }
        } else {
            // Show no internet message
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        // Implement your logic to get weather details based on location here
        fetchWeatherData(latitude,longitude)
    }
}
