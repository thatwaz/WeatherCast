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
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentCurrentWeatherBinding
import com.thatwaz.weathercast.model.location.LocationRepository
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.util.BarometricPressureColorUtility
import com.thatwaz.weathercast.util.ConversionUtility
import com.thatwaz.weathercast.util.PermissionUtils
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import java.util.*

class CurrentWeatherFragment : Fragment() {

    private lateinit var bottomNavView: BottomNavigationView
    private val viewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRepository: LocationRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        bottomNavView.visibility = View.VISIBLE
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        locationRepository.getCurrentLocation { latitude, longitude ->
            // Use the latitude and longitude to fetch the weather data
            getLocationWeatherDetails(latitude, longitude)
        }
    }

    private fun getWindDirection(degrees: Int): String {
        val directions = arrayOf(
            "N", "NNE", "NE", "ENE",
            "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW",
            "W", "WNW", "NW", "NNW"
        )

        val index = ((degrees + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }

    //TEMP
    private val imageResources = intArrayOf(
        R.drawable.img_clear_sky,
        R.drawable.img_cloudy,
        R.drawable.img_haze,
        R.drawable.img_isolated_clouds,
        R.drawable.img_partly_cloudy,
        R.drawable.img_broken_clouds,
        R.drawable.img_moon,
        R.drawable.img_full_moon,
        R.drawable.img_blood_moon,
        R.drawable.img_full_moon,
        R.drawable.img_night_clear
    )

    //TEMP
    private var currentImageIndex = 0

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected =
            networkInfo != null && networkInfo.isConnected && isInternetAvailable(connectivityManager)

        if (isConnected) {
            viewModel.fetchWeatherData(latitude, longitude)
        } else {
            showToast("No Internet Connection")
        }

        //TEMP
        binding.lblSunrise.setOnClickListener {
            currentImageIndex = (currentImageIndex + 1) % imageResources.size
            binding.ivCurrentWeatherImage.setImageResource(imageResources[currentImageIndex])
        }

        viewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            if (weatherData != null) {
                try {
                    handleWeatherData(weatherData)
                } catch (e: Exception) {
                    // Handle any exception that occurs during data processing
                    showToast("An error occurred while processing weather data.")
                    Log.e(TAG, "Error processing weather data: ${e.message}")
                }
            } else {
                showToast("No Internet Connection")
            }
        }
    }

    private fun handleWeatherData(weatherData: WeatherResponse) {
        val pressureInhPa = weatherData.main.pressure
        val pressureInInHg = ConversionUtility.hPaToInHg(pressureInhPa)
        val pressureColor = BarometricPressureColorUtility.getPressureColor(pressureInhPa)
        val currentConditions = weatherData.weather[0].description.capitalizeWords()
        val kelvinTemp = weatherData.main.temp
        val fahrenheitTemp = ConversionUtility.kelvinToFahrenheit(kelvinTemp)
        val humidityValue = weatherData.main.humidity
        val formattedHumidity = "$humidityValue%"
        val kelvinFeelsLikeTemp = weatherData.main.feelsLike
        val fahrenheitFeelsLikeTemp =
            ConversionUtility.kelvinToFahrenheit(kelvinFeelsLikeTemp).toString()
        val formattedFeelsLike = "$fahrenheitFeelsLikeTemp\u00B0"
        val windDirectionDegrees = weatherData.wind.deg
        val formattedWindDirection = getWindDirection(windDirectionDegrees)
        val visibilityInMeters = weatherData.visibility
        val visibilityInMiles = ConversionUtility.convertMetersToMiles(visibilityInMeters)
        val sunriseTime =
            ConversionUtility.convertUnixTimestampToTime(weatherData.sys.sunrise.toLong())
        val sunsetTime =
            ConversionUtility.convertUnixTimestampToTime(weatherData.sys.sunset.toLong())

        binding.apply {
            tvLocation.text = weatherData.name
            tvCurrentConditions.text = currentConditions
            tvFeelsLike.text = formattedFeelsLike
            tvCurrentTemperature.text = fahrenheitTemp.toString()
            tvHumidity.text = formattedHumidity
            tvWind.text = "$formattedWindDirection ${weatherData.wind.speed} mph "
            tvAirPressure.text = String.format("%.2f inHg", pressureInInHg.toDouble())
            binding.tvAirPressure.paint?.isUnderlineText = true
            binding.tvAirPressure.setTextColor(pressureColor)
            binding.tvAirPressure.setOnClickListener {
                showToast("Mornin!")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private fun String.capitalizeWords(): String = split(" ")
    .joinToString(" ") {
        it.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }










