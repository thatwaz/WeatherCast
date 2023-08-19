package com.thatwaz.weathercast.view.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R

import com.thatwaz.weathercast.databinding.FragmentForecastBinding

import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.data.LocationRepository
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.utils.error.Resource
import com.thatwaz.weathercast.view.ui.adapters.ForecastAdapter

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

//    private lateinit var fusedLocationClient: FusedLocationProviderClient


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

        val menuHost: MenuHost = requireActivity() as MenuHost

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_refresh, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_refresh -> {
                        // Call your ViewModel or other logic to refresh data
                        refreshForecastWeatherData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        locationRepository = LocationRepository(
            LocationServices.getFusedLocationProviderClient(requireContext())
        )
        // Initialize the WeatherDataHandler on the main thread
        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)

        // Set up RecyclerView
        setupRecyclerView()

        // Call fetchForecastData to initiate fetching the forecast data
        fetchForecastData()


        Log.i("MOH!", "Observing forecastData")
        viewModel.forecastData.observe(viewLifecycleOwner) { resource ->
            Log.i("MOH!", "Resource status: ${resource}")
            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state if needed
                    setWeatherDataVisibility(false)
                }
                is Resource.Success -> {
                    val dailyForecasts = resource.data
                    if (dailyForecasts != null) {
                        setWeatherDataVisibility(true)
                        // Update the RecyclerView with the new forecast data
                        binding.tvForecastLocation.text = dailyForecasts[0].cityName
                        updateRecyclerView(dailyForecasts)
                        Log.i("MOH!", "Daily diddly is $dailyForecasts")
                    }
                }
                is Resource.Error -> {
                    // Handle error state if needed
                    setWeatherDataVisibility(false)
                    val errorMessage = resource.errorMessage
                    Log.e("ForecastFragment", "Error fetching forecast data: $errorMessage")
                }
            }
        }


    }


    private fun fetchForecastData() {
        locationRepository.getCurrentLocation { latitude, longitude ->
            weatherDataHandler.fetchWeatherForecast(latitude, longitude,
                WeatherDataHandler.ForecastType.DAILY
            )
        }
    }

//    private fun requestLocationData() {
//        locationRepository.getCurrentLocation { latitude, longitude ->
//            weatherDataHandler.requestLocationData(latitude, longitude)
//        }
//    }

    private fun setupRecyclerView() {
        // Create the adapter
        forecastAdapter = ForecastAdapter()

        // Set up the RecyclerView with the adapter
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forecastAdapter
        }
    }

    private fun updateRecyclerView(dailyForecasts: List<DailyForecast>) {
        // Update the adapter's data with the new forecast list
        forecastAdapter.submitList(dailyForecasts)
        Log.i("MOH!", "Adapter data updated: $dailyForecasts")
    }
    private fun setWeatherDataVisibility(isVisible: Boolean) {
        binding.clLoading.visibility = if (isVisible) View.INVISIBLE else View.VISIBLE
        binding.clForecastTop.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.rvForecast.visibility = if (isVisible) View.VISIBLE else View.GONE

    }
    private fun refreshForecastWeatherData() {
        // Show the loading state (optional, if you want to indicate that data is being fetched)
        setWeatherDataVisibility(false)
//        binding.progressBar.visibility = View.VISIBLE

        // Request the weather data
        fetchForecastData()
    }


}