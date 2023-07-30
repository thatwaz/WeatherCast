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
import com.thatwaz.weathercast.utils.BarometricPressureColorUtil.getPressureColor
import com.thatwaz.weathercast.utils.ConversionUtil.breakTextIntoLines
import com.thatwaz.weathercast.utils.ConversionUtil.capitalizeWords
import com.thatwaz.weathercast.utils.ConversionUtil.convertMetersToMiles
import com.thatwaz.weathercast.utils.ConversionUtil.convertUnixTimestampToTime
import com.thatwaz.weathercast.utils.ConversionUtil.getWindDirection
import com.thatwaz.weathercast.utils.ConversionUtil.hPaToInHg
import com.thatwaz.weathercast.utils.ConversionUtil.kelvinToFahrenheit
import com.thatwaz.weathercast.utils.NetworkUtil
import com.thatwaz.weathercast.utils.PermissionUtil
import com.thatwaz.weathercast.utils.WeatherIconUtil
import com.thatwaz.weathercast.utils.WeatherTempUtils
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import kotlinx.coroutines.launch

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
            setWeatherDataVisibility(true)
            if (weatherData != null) {
                try {
                    handleWeatherData(weatherData)
                } catch (e: Exception) {
                    isErrorOccurred = true
                    Log.e(TAG, "Error processing weather data: ${e.message}")
                }
            } else {
                isErrorOccurred = true
            }
            if (isErrorOccurred) {
                showToast("An error has occurred")
            }
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("MOH!", "On view created")
        setWeatherDataVisibility(false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRepository = LocationRepository(fusedLocationClient)


        // Check location permissions and start updates
        checkLocationPermissions()

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun checkLocationPermissions() {
        PermissionUtil.requestLocationPermissions(
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
        locationRepository.getCurrentLocation { latitude, longitude ->
            getLocationWeatherDetails(latitude, longitude)
        }
    }

    private fun setCurrentWeatherImage(iconId: String) {
        val resourceId = WeatherIconUtil.getWeatherImageResource(iconId)
        binding.ivCurrentWeatherImage.setImageResource(resourceId)
    }

    private fun setCurrentWeatherIcon(iconId: String) {
        val resourceId = WeatherIconUtil.getWeatherIconResource(iconId)
        binding.ivCurrentWeatherIcon.setImageResource(resourceId)
    }

    private fun setWeatherDataVisibility(isVisible: Boolean) {
        binding.clLoading.visibility = if (isVisible) View.INVISIBLE else View.VISIBLE
        binding.clCurrentWeatherDetails.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.clLocation.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.clTop.visibility = if (isVisible) View.VISIBLE else View.GONE
    }


    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        if (_binding == null) {
            return
        }
        val connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
            showToast("No Internet Connection")
        }

        binding.lblSunrise.setOnClickListener {
            binding.ivCurrentWeatherImage.setImageResource(WeatherTempUtils.getNextImageResource())
        }
        //TEMP - Temporary testing for icons
        binding.lblSunset.setOnClickListener {
            binding.ivCurrentWeatherIcon.setImageResource(WeatherTempUtils.getNextIconResource())
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
        val imageIcon = weatherData.weather[0].icon

        setCurrentWeatherImage(imageIcon)
        setCurrentWeatherIcon(imageIcon)

        binding.apply {
            tvLocation.text = weatherData.name
            tvCurrentConditions.text = breakTextIntoLines(currentConditions,18)
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

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            getLocationWeatherDetails(lastLocation.latitude, lastLocation.longitude)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("MOH!", "View destroyed")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        if (_binding != null) {
            _binding = null
        }

    }
}













