package com.thatwaz.weathercast.view.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentHourlyBinding
import com.thatwaz.weathercast.model.data.LocationRepository
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.utils.error.Resource
import com.thatwaz.weathercast.view.ui.adapters.HourlyForecastAdapter
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// HourlyFragment.kt
class HourlyFragment : Fragment() {

    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var weatherDataHandler: WeatherDataHandler
    private lateinit var locationRepository: LocationRepository

    private val viewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentHourlyBinding? = null
    private val binding get() = _binding!!

    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        // Inflate the layout for this fragment
        _binding = FragmentHourlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationRepository = LocationRepository(
            LocationServices.getFusedLocationProviderClient(requireContext())
        )
        // Initialize the WeatherDataHandler on the main thread
        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)

        // Set up RecyclerView
        setupRecyclerView()

        // Call fetchForecastData to initiate fetching the forecast data
        fetchForecastData()

        viewModel.forecastData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
                is Resource.Success -> {
                    val forecastResponse = resource.data
                    if (forecastResponse != null) {
                        // Log the API response to understand its structure and data
                        Log.i("MOH!", "Forecast API Response: $forecastResponse")

                        // Implement your forecast data handling here based on the forecastResponse
                        // ...
                        // Update the RecyclerView with the new forecast data
                        updateRecyclerView(forecastResponse.list)
                    }
                }
                is Resource.Error -> {
                    // Handle error state if needed
                    val errorMessage = resource.errorMessage
                    Log.e("DOH!", "Error fetching forecast data: $errorMessage")
                }
            }
        }
    }

    private fun fetchForecastData() {
        locationRepository.getCurrentLocation { latitude, longitude ->
            weatherDataHandler.fetchWeatherForecast(latitude, longitude)
        }
    }

    private fun setupRecyclerView() {
        // Create the adapter
        hourlyForecastAdapter = HourlyForecastAdapter()

        // Set up the RecyclerView with the adapter
        binding.rvHourlyForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hourlyForecastAdapter
        }
    }

    private fun updateRecyclerView(forecastList: List<WeatherItem>) {
        // Update the adapter's data with the new forecast list
        hourlyForecastAdapter.submitList(forecastList)
        Log.i("MOH!", "list is $forecastList")
    }

}



//class HourlyFragment : Fragment() {
//
//    private lateinit var bottomNavView: BottomNavigationView
//    private lateinit var weatherDataHandler: WeatherDataHandler
//    private lateinit var locationRepository: LocationRepository
//
//    private val viewModel: WeatherViewModel by viewModels()
//    private var _binding: FragmentHourlyBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
//        // Inflate the layout for this fragment
//        _binding = FragmentHourlyBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        locationRepository = LocationRepository(
//            LocationServices.getFusedLocationProviderClient(requireContext())
//        )
//        // Initialize the WeatherDataHandler on the main thread
//        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)
//
//        // Call fetchForecastData to initiate fetching the forecast data
//        requestLocationData()
//        fetchForecastData()
//
//
//        viewModel.forecastData.observe(viewLifecycleOwner) { resource ->
//            when (resource) {
//                is Resource.Loading -> {
//                    // Handle loading state if needed
//                }
//                is Resource.Success -> {
//                    val forecastResponse = resource.data
//                    if (forecastResponse != null) {
//                        // Log the API response to understand its structure and data
//                        Log.i("MOH!", "Forecast API Response: $forecastResponse")
//
//                        // Implement your forecast data handling here based on the forecastResponse
//                        // ...
//                    }
//                }
//                is Resource.Error -> {
//                    // Handle error state if needed
//                    val errorMessage = resource.errorMessage
//                    Log.e("DOH!", "Error fetching forecast data: $errorMessage")
//                }
//            }
//        }
//    }
//    private fun fetchForecastData() {
//        requestLocationData()
//    }
//    private fun requestLocationData() {
//        locationRepository.getCurrentLocation { latitude, longitude ->
//            weatherDataHandler.fetchWeatherForecast(latitude, longitude)
//        }
//    }
//
//    // ... (rest of the code)
//}


//class HourlyFragment : Fragment() {
//
//    private lateinit var bottomNavView: BottomNavigationView
//    private lateinit var weatherDataHandler: WeatherDataHandler
//
//    private val viewModel: WeatherViewModel by viewModels()
//    private var _binding: FragmentHourlyBinding? = null
//    private val binding get() = _binding!!
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
//        // Inflate the layout for this fragment
//        _binding = FragmentHourlyBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        // Call fetchForecastData to initiate fetching the forecast data
//        // Pass the latitude and longitude to the function
////        viewModel.fetchForecastData(latitude, longitude)
//        // Launch a coroutine in the lifecycle scope of the fragment
//        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
//            // Call fetchForecastData to initiate fetching the forecast data
//            // Pass the latitude and longitude to the function
//            weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)
//            fetchForecastData()
//        }
//
//        viewModel.forecastData.observe(viewLifecycleOwner) { resource ->
//            when (resource) {
//                is Resource.Loading -> {
//                    // Handle loading state if needed
//                }
//                is Resource.Success -> {
//                    val forecastResponse = resource.data
//                    if (forecastResponse != null) {
//                        // Log the API response to understand its structure and data
//                        Log.i("DOH!", "Forecast API Response: $forecastResponse")
//
//                        // Implement your forecast data handling here based on the forecastResponse
//                        // ...
//                    }
//                }
//                is Resource.Error -> {
//                    // Handle error state if needed
//                    val errorMessage = resource.errorMessage
//                    Log.e("DOH!", "Error fetching forecast data: $errorMessage")
//                }
//            }
//        }
//    }
//    private fun fetchForecastData() {
//        // Use latitude and longitude values appropriate for your location
//        val latitude = 37.7749
//        val longitude = -122.4194
//
//        // Call fetchForecastData to initiate fetching the forecast data
//        weatherDataHandler.requestLocationData(latitude, longitude)
//    }
//}


