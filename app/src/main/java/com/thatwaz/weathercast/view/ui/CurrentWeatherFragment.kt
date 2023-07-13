package com.thatwaz.weathercast.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.thatwaz.weathercast.databinding.FragmentCurrentWeatherBinding





import com.google.android.gms.location.*
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import java.util.*

class CurrentWeatherFragment : Fragment() {
    private val viewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            // Update the UI with the weather data
            // Use binding to access the views in your fragment layout
            binding.tvCurrentLocation.text = weatherData?.name
            binding.tvCurrentWeatherDescription.text = weatherData?.weather?.get(0)?.description
            val kelvinTemp = weatherData?.main!!.temp
            val fahrenheitTemp = kelvinTemp.let { (it - 273.15) * 9/5 + 32 }
            binding.tvCurrentTemperature.text = fahrenheitTemp.toInt().toString() + " \u00B0"
            // ... update other views ...
        }

//        viewModel.weatherData.observe(viewLifecycleOwner) { weatherResponse ->
//            if (weatherResponse != null) {
//                Log.i("DOH!", "Output is $weatherResponse")
//            } else {
//                Log.i("DOH!", "Failed to receive weather data or empty response")
//            }
//        }



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation: Location = locationResult.lastLocation
                val latitude = lastLocation.latitude

                Log.i("Current Latitude", "$latitude")
                val longitude = lastLocation.longitude
                Log.i("Current Longitude", "$longitude")
                getLocationWeatherDetails(latitude, longitude)
            }
        }
        checkLocationPermissionsAndStartUpdates()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun requestLocationPermissions() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report?.areAllPermissionsGranted() == true) {
                        requestLocationData()
                    }
                    if (report?.isAnyPermissionPermanentlyDenied == true) {
                        showToast("You have denied permission")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }
            })
            .onSameThread()
            .check()
    }

    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage("It looks like you have turned off permissions")
            .setPositiveButton("Go To Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", requireContext().packageName, null)
            intent.data = uri
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 100000
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.myLooper()
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun checkLocationPermissionsAndStartUpdates() {
        if (!isLocationEnabled()) {
            showToast("Your location provider is turned off, please turn it on")
            openLocationSettings()
        } else {
            requestLocationPermissions()
        }
    }

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        viewModel.fetchWeatherData(latitude, longitude)
        // TODO: Implement fetching weather details using the provided latitude and longitude
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        _binding = null
    }
}








