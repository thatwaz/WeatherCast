package com.thatwaz.weathercast.view.ui


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentCurrentWeatherBinding
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.data.LocationRepository
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import com.thatwaz.weathercast.model.weatherresponse.WeatherResponse
import com.thatwaz.weathercast.utils.BarometricPressureColorUtil.getPressureColor
import com.thatwaz.weathercast.utils.ConversionUtil.breakTextIntoLines
import com.thatwaz.weathercast.utils.ConversionUtil.capitalizeWords
import com.thatwaz.weathercast.utils.ConversionUtil.convertMetersToMiles
import com.thatwaz.weathercast.utils.ConversionUtil.convertUnixTimestampToTimeWithAMPM
import com.thatwaz.weathercast.utils.ConversionUtil.getWindDirection
import com.thatwaz.weathercast.utils.ConversionUtil.hPaToInHg
import com.thatwaz.weathercast.utils.ConversionUtil.kelvinToFahrenheit
import com.thatwaz.weathercast.utils.PermissionUtil
import com.thatwaz.weathercast.utils.WeatherIconUtil
import com.thatwaz.weathercast.utils.error.Resource
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import javax.inject.Inject

class CurrentWeatherFragment : Fragment() {

    @Inject
    lateinit var viewModel: WeatherViewModel

    private lateinit var bottomNavView: BottomNavigationView
    //    private val viewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRepository: LocationRepository
    private lateinit var weatherDataHandler: WeatherDataHandler

    private var isErrorOccurred = false


//    private fun refreshContent() {
//        // Your logic to fetch or update the data. Typically, you will call some method from your ViewModel here.
//        viewModel.refreshWeatherData()
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        bottomNavView.visibility = View.VISIBLE
        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
        (activity?.application as WeatherCastApplication).appComponent.inject(this)
        viewModel.weatherData.removeObservers(viewLifecycleOwner)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity() as MenuHost

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_refresh, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_refresh -> {
                        // Call your ViewModel or other logic to refresh data
                        refreshCurrentWeatherData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        Log.i("MOH!", "On view created")
        setWeatherDataVisibility(false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRepository = LocationRepository(fusedLocationClient)
        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)
        // Check location permissions and start updates
        observeWeatherData()
        checkLocationPermissions()
    }

    private fun updateWeatherUI(weatherData: WeatherResponse) {
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
        val sunriseTime = convertUnixTimestampToTimeWithAMPM(weatherData.sys.sunrise.toLong())
        val sunsetTime = convertUnixTimestampToTimeWithAMPM(weatherData.sys.sunset.toLong())
        val imageIcon = weatherData.weather[0].icon

        setCurrentWeatherImage(imageIcon)
        setCurrentWeatherIcon(imageIcon)

        binding.apply {
            tvLocation.text = weatherData.name
            tvCurrentConditions.text = breakTextIntoLines(currentConditions, 18)
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

    private fun observeWeatherData() {
        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    setWeatherDataVisibility(true)
                    handleWeatherData(resource.data!!)
                }
                is Resource.Error -> {
                    isErrorOccurred = true
                    setWeatherDataVisibility(false)
                    resource.errorMessage?.let { showErrorToast(it) } // Show the error message
                }
                is Resource.Loading -> {
                    setWeatherDataVisibility(false)
                }
            }
        }
    }

    private fun showErrorToast(errorMessage: String) {
        showToast(errorMessage)
    }

    private fun requestLocationData() {
        locationRepository.getCurrentLocation { _, _ ->
            weatherDataHandler.requestLocationData()
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

    private fun handleWeatherData(weatherData: WeatherResponse) {
        updateWeatherUI(weatherData)
    }

    private fun refreshCurrentWeatherData() {
        // Show the loading state (optional, if you want to indicate that data is being fetched)
        setWeatherDataVisibility(false)
//        binding.progressBar.visibility = View.VISIBLE

        // Request the weather data
        requestLocationData()
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
//            val lastLocation = locationResult.lastLocation
            weatherDataHandler.requestLocationData()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the location callback when the view is destroyed
        fusedLocationClient.removeLocationUpdates(locationCallback)
        // Nullify the binding to avoid potential memory leaks
        _binding = null
    }
}

