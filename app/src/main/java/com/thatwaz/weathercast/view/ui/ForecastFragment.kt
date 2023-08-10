package com.thatwaz.weathercast.view.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R

import com.thatwaz.weathercast.databinding.FragmentForecastBinding

import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.data.LocationRepository
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.utils.error.Resource
import com.thatwaz.weathercast.view.ui.adapters.ForecastAdapter

import com.thatwaz.weathercast.view.ui.adapters.HourlyAdapter
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import javax.inject.Inject


class ForecastFragment : Fragment() {


    @Inject
    lateinit var viewModel: WeatherViewModel

    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var weatherDataHandler: WeatherDataHandler
    private lateinit var locationRepository: LocationRepository

    private var _binding: FragmentForecastBinding? = null
    private val binding get() = _binding!!

    private lateinit var forecastAdapter: ForecastAdapter

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        // Inflate the layout for this fragment
        (activity?.application as WeatherCastApplication).appComponent.inject(this)
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        Log.i("Forecast", "view created")
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
        forecastAdapter = ForecastAdapter()

        // Set up the RecyclerView with the adapter
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forecastAdapter
        }
    }

    private fun updateRecyclerView(forecastList: List<WeatherItem>) {
        // Update the adapter's data with the new forecast list
        forecastAdapter.submitList(forecastList)
        Log.i("MOH!", "list is $forecastList")
    }


}