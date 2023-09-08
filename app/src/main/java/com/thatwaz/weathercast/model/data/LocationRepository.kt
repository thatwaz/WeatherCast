package com.thatwaz.weathercast.model.data

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import com.google.android.gms.location.*
import javax.inject.Inject


class LocationRepository @Inject constructor(private val fusedLocationClient: FusedLocationProviderClient) {

    private var locationCallback: LocationCallback? = null  // make it an instance variable

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
        Log.d("LocationRepository", "getCurrentLocation - Start")
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
                Log.d("LocationRepository", "onLocationResult received in ${end - start} ms")
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
        Log.i("MOH!", "Repository removeLocationUpdates called")
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null // This may help in releasing the memory
        }
    }
}


//        val locationRequest = LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            smallestDisplacement = 1000f // Set the minimum displacement (in meters) for updates
//        }

//class LocationRepository(private val fusedLocationClient: FusedLocationProviderClient) {
//
//    @SuppressLint("MissingPermission")
//    fun getCurrentLocation(callback: (latitude: Double, longitude: Double) -> Unit) {
//        val locationRequest = LocationRequest.create().apply {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            smallestDisplacement = 1000f // Set the minimum displacement (in meters) for updates
//        }
//
//        val locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                val lastLocation: Location? = locationResult.lastLocation
//                if (lastLocation != null) {
//                    callback(lastLocation.latitude, lastLocation.longitude)
//                }
//            }
//        }
//
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            null
//        )
//    }
//}