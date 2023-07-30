package com.thatwaz.weathercast.model.data

import com.google.android.gms.location.FusedLocationProviderClient
import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationRepository(private val fusedLocationClient: FusedLocationProviderClient) {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 100000
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation: Location? = locationResult.lastLocation
                if (lastLocation != null) {
                    callback(lastLocation.latitude, lastLocation.longitude)
                } else {
                    // Handle the case when location is null or unavailable
                    // For example, you can show an error message or provide a default location
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

}