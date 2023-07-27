package com.thatwaz.weathercast.view.ui


import android.content.ContentValues.TAG
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentCurrentWeatherBinding
import com.thatwaz.weathercast.model.location.LocationRepository
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.util.BarometricPressureColorUtility.getPressureColor
import com.thatwaz.weathercast.util.ConversionUtility.convertMetersToMiles
import com.thatwaz.weathercast.util.ConversionUtility.convertUnixTimestampToTime
import com.thatwaz.weathercast.util.ConversionUtility.getWindDirection
import com.thatwaz.weathercast.util.ConversionUtility.hPaToInHg
import com.thatwaz.weathercast.util.ConversionUtility.kelvinToFahrenheit
import com.thatwaz.weathercast.util.PermissionUtils
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch
import java.util.*

class CurrentWeatherFragment : Fragment() {


    private lateinit var bottomNavView: BottomNavigationView
    private val viewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRepository: LocationRepository

    private var isErrorOccurred = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        bottomNavView.visibility = View.VISIBLE
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)

        viewModel.weatherData.removeObservers(viewLifecycleOwner)
        viewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            binding.progressBar.visibility = View.GONE
            if (weatherData != null) {
                try {
                    handleWeatherData(weatherData)
                } catch (e: Exception) {
                    // Handle any exception that occurs during data processing
                    isErrorOccurred = true
                    Log.e(TAG, "Error processing weather data: ${e.message}")
                }
            } else {
                isErrorOccurred = true
            }

            // Show the toast here outside of the check
            if (isErrorOccurred) {
                showToast("An error has occurred")
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("MOH!", "On view created")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRepository = LocationRepository(fusedLocationClient)

        // Check location permissions and start updates
        checkLocationPermissions()

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun checkLocationPermissions() {
        PermissionUtils.requestLocationPermissions(
            requireContext(),
            { // On permission granted
                requestLocationData()
            },
            { // On permission denied
                showToast("You have denied permission")
            }
        )
    }

    private fun requestLocationData() {
        // Get the current location using the LocationRepository
        binding.progressBar.visibility = View.VISIBLE
        locationRepository.getCurrentLocation { latitude, longitude ->
            // Use the latitude and longitude to fetch the weather data
            getLocationWeatherDetails(latitude, longitude)
        }
    }

    //TEMP
    private val imageResources = intArrayOf(
        R.drawable.img_clear_sky,
        R.drawable.img_cloudy,
        R.drawable.img_haze,
        R.drawable.img_isolated_clouds,
        R.drawable.img_partly_cloudy,
        R.drawable.img_broken_clouds,
        R.drawable.img_thunderstorm,
        R.drawable.img_mostly_cloudy,
        R.drawable.img_mist,
        R.drawable.img_night_clear
    )

    //TEMP
    private var currentImageIndex = 0


    //TEMP
    private val iconResources = intArrayOf(
        R.drawable.day_partly_cloudy,
        R.drawable.day_rain,
        R.drawable.day_showers,
        R.drawable.day_sunny,
        R.drawable.day_thunderstorm,
        R.drawable.day_snow,
        R.drawable.night_clear,
        R.drawable.night_cloudy,
        R.drawable.night_rain,
        R.drawable.night_showers,
        R.drawable.night_thunderstorm,
        R.drawable.night_snow,
    )
    //TEMP
    private var currentIconIndex = 0

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (_binding == null) {
            return
        }
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected =
            networkInfo != null && networkInfo.isConnected && isInternetAvailable(
                connectivityManager
            )

        if (isConnected) {
            viewModel.viewModelScope.launch {
                viewModel.fetchWeatherData(latitude, longitude)
            }
        } else {
            showToast("No Internet Connection")
        }

        //TEMP
        binding.lblSunrise.setOnClickListener {
            currentImageIndex = (currentImageIndex + 1) % imageResources.size
            binding.ivCurrentWeatherImage.setImageResource(imageResources[currentImageIndex])
        }
        //TEMP
        binding.lblSunset.setOnClickListener {
            currentIconIndex = (currentIconIndex + 1) % iconResources.size
            binding.ivCurrentWeatherIcon.setImageResource(iconResources[currentIconIndex])
        }


    }

    private fun handleWeatherData(weatherData: WeatherResponse) {
        val pressureInhPa = weatherData.main.pressure
        val pressureInInHg = hPaToInHg(pressureInhPa)
        val pressureColor = getPressureColor(pressureInhPa)
        val currentConditions = weatherData.weather[0].description.capitalizeWords()
        val kelvinTemp = weatherData.main.temp
        val fahrenheitTemp = kelvinToFahrenheit(kelvinTemp)
        val humidityValue = weatherData.main.humidity
        val formattedHumidity = "$humidityValue%"
        val kelvinFeelsLikeTemp = weatherData.main.feelsLike
        val fahrenheitFeelsLikeTemp = kelvinToFahrenheit(kelvinFeelsLikeTemp).toString()
        val formattedFeelsLike = "$fahrenheitFeelsLikeTemp\u00B0"
        val windDirectionDegrees = weatherData.wind.deg
        val formattedWindDirection = getWindDirection(windDirectionDegrees)
        val visibilityInMeters = weatherData.visibility
        val visibilityInMiles = convertMetersToMiles(visibilityInMeters)
        val sunriseTime = convertUnixTimestampToTime(weatherData.sys.sunrise.toLong())
        val sunsetTime = convertUnixTimestampToTime(weatherData.sys.sunset.toLong())

        binding.apply {
            tvLocation.text = weatherData.name
            tvCurrentConditions.text = currentConditions
            tvFeelsLike.text = formattedFeelsLike
            tvCurrentTemperature.text = fahrenheitTemp.toString()
            tvHumidity.text = formattedHumidity
            tvWind.text = "$formattedWindDirection ${weatherData.wind.speed} mph "
            tvAirPressure.text = String.format("%.2f", pressureInInHg.toDouble())
            binding.tvAirPressure.paint?.isUnderlineText = true
            binding.tvAirPressure.setTextColor(pressureColor)
            binding.tvAirPressure.setOnClickListener {
                val action = CurrentWeatherFragmentDirections
                    .actionCurrentWeatherFragmentToBarometricPressureDialogFragment()
                findNavController().navigate(action)
            }
            tvVisibility.text = String.format("%.2f miles", visibilityInMiles)
            tvSunrise.text = sunriseTime
            tvSunset.text = sunsetTime
        }

        binding.clLocation.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Latitude " + weatherData.coord.lat.toString()
                        + "\nLongitude " + weatherData.coord.lon.toString(),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager
                .getNetworkCapabilities(connectivityManager.activeNetwork)
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        } else {
            // For devices with API level < 23 (M)
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            getLocationWeatherDetails(lastLocation.latitude, lastLocation.longitude)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Check if _binding is not null before setting it to null
        Log.i("MOH!", "View destroyed")
        fusedLocationClient.removeLocationUpdates(locationCallback)
//        viewModel.weatherData.removeObservers(viewLifecycleOwner)
        if (_binding != null) {
            _binding = null
        }

    }
}


private fun String.capitalizeWords(): String = split(" ")
    .joinToString(" ") {
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }










