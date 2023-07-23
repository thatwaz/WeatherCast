package com.thatwaz.weathercast.view.ui



import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentCurrentWeatherBinding
import com.thatwaz.weathercast.model.location.LocationRepository
import com.thatwaz.weathercast.model.util.PermissionUtils
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
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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

    private fun kelvinToFahrenheit(kelvinTemp: Double): Int {
        return ((kelvinTemp - 273.15) * 9 / 5 + 32).toInt()
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
    private fun hPaToInHg(hPaPressure: Int): String {
        return (hPaPressure / 33.8639).toString()
    }
    private fun isBarometricPressureGood(pressure: Int): Boolean {
        // Define the ranges for good and bad barometric pressure
        val goodPressureRange = 980..1030 // You can adjust these values based on your criteria
        val badPressureRange = 950..979   // You can adjust these values based on your criteria

        // Check if the pressure falls within the good or bad range
        return pressure in goodPressureRange
    }



    private fun convertMetersToMiles(visibilityInMeters: Int): Double {
        return visibilityInMeters / 1609.34 // 1 mile = 1609.34 meters
    }
    private fun convertUnixTimestampToTime(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(unixTimestamp * 1000)
        return dateFormat.format(date)
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
        viewModel.fetchWeatherData(latitude, longitude)

        //TEMP
        binding.lblSunrise.setOnClickListener {
            currentImageIndex = (currentImageIndex + 1) % imageResources.size
            binding.ivCurrentWeatherImage.setImageResource(imageResources[currentImageIndex])
        }

        viewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            if (weatherData != null) {

                val pressureInhPa = weatherData.main.pressure
                val pressureInInHg = hPaToInHg(pressureInhPa)
                val isPressureGood = isBarometricPressureGood(pressureInhPa)
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
                val redColorHex = "#FF1744"
                val greenColorHex = "#00C853"
                val redColor = Color.parseColor(redColorHex)
                val greenColor = Color.parseColor(greenColorHex)
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
                    tvWind.text = "${weatherData.wind.speed} mph $formattedWindDirection"
                    tvAirPressure.text = String.format("%.2f inHg", pressureInInHg.toDouble())
                    if (isPressureGood)      {
                        binding.tvAirPressure.setTextColor(greenColor)
                    }   else {
                        binding.tvAirPressure.setTextColor(redColor)
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
            } else {
                showToast("No Internet Connection")
            }
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
            if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()
            ) else it.toString()
        }
    }









