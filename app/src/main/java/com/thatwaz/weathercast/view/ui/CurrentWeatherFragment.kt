package com.thatwaz.weathercast.view.ui



import android.os.Bundle
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

    private fun getLocationWeatherDetails(latitude: Double, longitude: Double) {
        viewModel.fetchWeatherData(latitude, longitude)

        viewModel.weatherData.observe(viewLifecycleOwner) { weatherData ->
            if (weatherData != null) {
                val currentConditions = weatherData.weather[0].description.capitalizeWords()
                val kelvinTemp = weatherData.main.temp
                val fahrenheitTemp = kelvinToFahrenheit(kelvinTemp)
                val humidityValue = weatherData.main.humidity
                val formattedHumidity = "$humidityValue%"
                val kelvinFeelsLikeTemp = weatherData.main.feelsLike
                val fahrenheitFeelsLikeTemp = kelvinToFahrenheit(kelvinFeelsLikeTemp).toString()
                val formattedFeelsLike = "$fahrenheitFeelsLikeTemp\u00B0"

                binding.apply {
                    tvLocation.text = weatherData.name
                    tvCurrentConditions.text = currentConditions
                    tvFeelsLike.text = formattedFeelsLike
                    tvCurrentTemperature.text = fahrenheitTemp.toInt().toString()
                    tvHumidity.text = formattedHumidity
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








