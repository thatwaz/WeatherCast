package com.thatwaz.weathercast.view.ui


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentCurrentWeatherBinding
import com.thatwaz.weathercast.model.application.WeatherCastApplication
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
import kotlin.math.ceil


//class CurrentWeatherFragment : Fragment() {
//
//    @Inject
//    lateinit var viewModel: WeatherViewModel
//
//    @Inject
//    lateinit var locationRepository: LocationRepository
//
//    @Inject
//    lateinit var weatherDataHandler: WeatherDataHandler
//
////    @Inject
////    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//
//    private var _binding: FragmentCurrentWeatherBinding? = null
//    private val binding get() = _binding!!
//
////    private lateinit var weatherDataHandler: WeatherDataHandler
//    private lateinit var bottomNavView: BottomNavigationView
//
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
//        bottomNavView.visibility = View.VISIBLE
//
//        _binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false)
//        (activity?.application as WeatherCastApplication).appComponent.inject(this)
//
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupMenu()
//        initializeComponents()
//        observeWeatherData()
//        checkLocationPermissions()
//    }
//
//    private fun setupMenu() {
//        val menuHost: MenuHost = requireActivity()
//
//        menuHost.addMenuProvider(object : MenuProvider {
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_refresh, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
//                R.id.action_refresh -> {
//                    refreshCurrentWeatherData()
//                    true
//                }
//                else -> false
//            }
//        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
//    }
//
//    private fun initializeComponents() {
//        setWeatherDataVisibility(false)
//        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)
//    }
//
//    private fun observeWeatherData() {
//        viewModel.weatherData.observe(viewLifecycleOwner) { resource ->
//            when (resource) {
//                is Resource.Success -> {
//                    setWeatherDataVisibility(true)
//                    updateWeatherUI(resource.data!!)
//                }
//                is Resource.Error -> {
//                    setWeatherDataVisibility(false)
//                    resource.errorMessage?.let { showToast(it) }
//                }
//                is Resource.Loading -> {
//                    setWeatherDataVisibility(false)
//                }
//            }
//        }
//    }
//
//    private fun setCurrentWeatherImage(iconId: String) {
//        val resourceId = WeatherIconUtil.getWeatherImageResource(iconId)
//        binding.ivCurrentWeatherImage.setImageResource(resourceId)
//    }
//
//    private fun setCurrentWeatherIcon(iconId: String) {
//        val resourceId = WeatherIconUtil.getWeatherIconResource(iconId)
//        binding.ivCurrentWeatherIcon.setImageResource(resourceId)
//    }
//
//    private fun setWeatherDataVisibility(isVisible: Boolean) {
//        val visibilityStatus = if (isVisible) View.VISIBLE else View.GONE
//        with(binding) {
//            listOf(clLoading, clCurrentWeatherDetails, clLocation, clTop).forEach { view ->
//                view.visibility = visibilityStatus
//            }
//        }
//    }
//
//    private fun updateWeatherUI(weatherData: WeatherResponse) {
//
//        val pressureInhPa = weatherData.main.pressure
//        val pressureInInHg = hPaToInHg(pressureInhPa)
//        val pressureColor = getPressureColor(pressureInhPa)
//        val currentConditions = weatherData.weather[0].description.capitalizeWords()
//        val kelvinTemp = weatherData.main.temp
//        val fahrenheitTemp = kelvinToFahrenheit(kelvinTemp)
//        val humidityValue = weatherData.main.humidity
//        val formattedHumidity = "$humidityValue%"
//        val kelvinFeelsLikeTemp = weatherData.main.feelsLike
//        val fahrenheitFeelsLikeTemp = kelvinToFahrenheit(kelvinFeelsLikeTemp).toString()
//        val formattedFeelsLike = "$fahrenheitFeelsLikeTemp\u00B0"
//        val windDirectionDegrees = weatherData.wind.deg
//        val formattedWindDirection = getWindDirection(windDirectionDegrees)
//        val visibilityInMeters = weatherData.visibility
//        val visibilityInMiles = convertMetersToMiles(visibilityInMeters)
//        val sunriseTime = convertUnixTimestampToTimeWithAMPM(weatherData.sys.sunrise.toLong())
//        val sunsetTime = convertUnixTimestampToTimeWithAMPM(weatherData.sys.sunset.toLong())
//        val imageIcon = weatherData.weather[0].icon
//
//        setCurrentWeatherImage(imageIcon)
//        setCurrentWeatherIcon(imageIcon)
//
//        binding.apply {
//            tvLocation.text = weatherData.name
//            tvCurrentConditions.text = breakTextIntoLines(currentConditions, 18)
//            tvFeelsLike.text = formattedFeelsLike
//            tvCurrentTemperature.text = fahrenheitTemp.toString()
//            tvHumidity.text = formattedHumidity
//            tvWind.text = buildString {
//                append(formattedWindDirection)
//                append(" ")
//                append(ceil(weatherData.wind.speed).toInt())
//                append(" mph ")
//            }
//            tvAirPressure.text = String.format("%.2f", pressureInInHg.toDouble())
//            binding.tvAirPressure.paint?.isUnderlineText = true
//            binding.tvAirPressure.setTextColor(pressureColor)
//            binding.tvAirPressure.setOnClickListener {
//                val action = CurrentWeatherFragmentDirections
//                    .actionCurrentWeatherFragmentToBarometricPressureDialogFragment()
//                findNavController().navigate(action)
//            }
//            tvVisibility.text = buildString {
//                append(visibilityInMiles)
//                append(" miles")
//            }
//            tvSunrise.text = sunriseTime
//            tvSunset.text = sunsetTime
//        }
//    }
//
//    private fun showToast(message: String) {
//        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
//    }
//
//    private fun checkLocationPermissions() {
//        PermissionUtil.requestLocationPermissions(
//            requireContext(),
//            { requestLocationData() },
//            { showToast("You have denied permission") }
//        )
//    }
//
//    private fun requestLocationData() {
//        locationRepository.getCurrentLocation { _, _ -> weatherDataHandler.requestLocationData() }
//    }
//
//    private fun refreshCurrentWeatherData() {
//        setWeatherDataVisibility(false)
//        requestLocationData()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        viewModel.weatherData.removeObservers(viewLifecycleOwner)
//        weatherDataHandler.cleanUp()
//        Log.i("MOH!", "CW removeLocationUpdates called")
//        locationRepository.removeLocationUpdates()
//        _binding = null
//    }
//}


class CurrentWeatherFragment : Fragment() {


    private val TAG = "Performance"

    @Inject
    lateinit var viewModel: WeatherViewModel

//    @Inject
//    lateinit var locationRepository: LocationRepository

    private lateinit var bottomNavView: BottomNavigationView

    private var _binding: FragmentCurrentWeatherBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

//    @Inject
//    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    private lateinit var locationRepository: LocationRepository
//    private lateinit var weatherDataHandler: WeatherDataHandler


    private lateinit var weatherDataHandler: WeatherDataHandler

    private var isErrorOccurred = false


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

        val startTime = System.currentTimeMillis()
        super.onViewCreated(view, savedInstanceState)
        val menuHost: MenuHost = requireActivity()
        Log.i("Current", "Rendering Current Fragment")
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_refresh, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_refresh -> {
                        refreshCurrentWeatherData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setWeatherDataVisibility(false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
//        locationRepository = LocationRepository(fusedLocationClient)
        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)

        checkLocationPermissions()
        observeWeatherData()
        val endTime = System.currentTimeMillis()
        Log.d(TAG, "onViewCreated took ${endTime - startTime}ms")
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
        Log.d("WeatherDataHandler", "requestLocationData - Start")
        val start = System.currentTimeMillis()
        val startTime = System.currentTimeMillis()
//        locationRepository.getCurrentLocation { _, _ ->
        val end = System.currentTimeMillis()
        Log.d("WeatherDataHandler", "Received location data in ${end - start} ms")
        weatherDataHandler.requestLocationData()
        Log.i("Current", "Getting location")
        val endTime = System.currentTimeMillis()
        Log.d(TAG, "requestLocationData took ${endTime - startTime}ms")
//        }
    }

    private fun updateWeatherUI(weatherData: WeatherResponse) {

        val startTime = System.currentTimeMillis()
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
        //uses device location (cannot use mock GPS for accurate sunrise time)
//        val sunriseTime = convertUnixTimestampToTimeWithAMPM(weatherData.sys.sunrise.toLong())
        val sunriseTime = convertUnixTimestampToTimeWithAMPM(
            weatherData.sys.sunrise.toLong(),
//            weatherData.timezone
        )

        //uses device location (cannot use mock GPS for accurate sunset time)
//        val sunsetTime = convertUnixTimestampToTimeWithAMPM(weatherData.sys.sunset.toLong())
        val sunsetTime = convertUnixTimestampToTimeWithAMPM(
            weatherData.sys.sunset.toLong(),
//            weatherData.timezone
        )
        val imageIcon = weatherData.weather[0].icon

        setCurrentWeatherImage(imageIcon)
        setCurrentWeatherIcon(imageIcon)

        binding.apply {
            tvLocation.text = weatherData.name
            tvCurrentConditions.text = breakTextIntoLines(currentConditions, 18)
            tvFeelsLike.text = formattedFeelsLike
            tvCurrentTemperature.text = fahrenheitTemp.toString()
            tvHumidity.text = formattedHumidity
            tvWind.text = buildString {
                append(formattedWindDirection)
                append(" ")
                append(ceil(weatherData.wind.speed).toInt())
                append(" mph ")
            }
            tvAirPressure.text = String.format("%.2f", pressureInInHg.toDouble())
            binding.tvAirPressure.paint?.isUnderlineText = true
            binding.tvAirPressure.setTextColor(pressureColor)
            binding.tvAirPressure.setOnClickListener {
                val action = CurrentWeatherFragmentDirections
                    .actionCurrentWeatherFragmentToBarometricPressureDialogFragment()
                findNavController().navigate(action)
            }
            tvVisibility.text = buildString {
                append(visibilityInMiles)
                append(" miles")
            }
            tvSunrise.text = sunriseTime
            tvSunset.text = sunsetTime
        }
        val endTime = System.currentTimeMillis()
        Log.d(TAG, "updateWeatherUI took ${endTime - startTime}ms")
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
        setWeatherDataVisibility(false)
        requestLocationData()
    }


//    private val locationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            weatherDataHandler.requestLocationData()
//        }
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.weatherData.removeObservers(viewLifecycleOwner)
        weatherDataHandler.cleanUp()
        Log.i("MOH!", "CW removeLocationUpdates called")
//        locationRepository.removeLocationUpdates()
        _binding = null
    }
}
