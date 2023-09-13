package com.thatwaz.weathercast.model.data

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.*
import javax.inject.Inject


class LocationRepository @Inject constructor(private val fusedLocationClient: FusedLocationProviderClient) {

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        val start = System.currentTimeMillis()
        // Remove any existing location updates to ensure we're not creating multiple callbacks
        //(prevents memory leak)
        removeLocationUpdates()
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
                .apply {
                    setWaitForAccurateLocation(false)
                    setMinUpdateIntervalMillis(5 * 60 * 1000)  // 5 minutes in milliseconds
                    setMaxUpdateDelayMillis(5 * 60 * 1000)  // 5 minutes in milliseconds
                    setMinUpdateDistanceMeters(1000f) // Minimum distance for update in meters
                }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val end = System.currentTimeMillis()
                val lastLocation: Location? = locationResult.lastLocation
                if (lastLocation != null) {
                    callback(lastLocation.latitude, lastLocation.longitude)
                }
            }
        }

        locationCallback?.let {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                it,
                null
            )
        }
    }

    fun removeLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null // This may help in releasing the memory
        }
    }
}

