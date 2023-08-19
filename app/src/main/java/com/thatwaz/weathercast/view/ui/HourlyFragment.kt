package com.thatwaz.weathercast.view.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentHourlyBinding
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.data.LocationRepository
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.utils.PermissionUtil
import com.thatwaz.weathercast.utils.error.Resource
import com.thatwaz.weathercast.view.ui.adapters.HourlyAdapter
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import javax.inject.Inject


// HourlyFragment.kt
class HourlyFragment : Fragment() {

    @Inject
    lateinit var viewModel: WeatherViewModel

    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var weatherDataHandler: WeatherDataHandler
    private lateinit var locationRepository: LocationRepository

//    private val viewModel: WeatherViewModel by viewModels()
    private var _binding: FragmentHourlyBinding? = null
    private val binding get() = _binding!!

    private lateinit var hourlyAdapter: HourlyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        // Inflate the layout for this fragment
        (activity?.application as WeatherCastApplication).appComponent.inject(this)
        _binding = FragmentHourlyBinding.inflate(inflater, container, false)
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
                        refreshHourlyWeatherData()
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
        fetchForecastData()
        // Set up RecyclerView
        setupRecyclerView()



        viewModel.hourlyData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Handle loading state if needed
                    setWeatherDataVisibility(false)
                }
                is Resource.Success -> {
                    val forecastResponse = resource.data
                    if (forecastResponse != null) {
                        setWeatherDataVisibility(true)
                        // Log the API response to understand its structure and data
                        Log.i("MOH!", "Forecast API Response: $forecastResponse")
                        // prob need to put in function______________________________________________________
                        binding.tvHourlyLocation.text = forecastResponse.city.name

                        // Implement your forecast data handling here based on the forecastResponse
                        // ...
                        // Update the RecyclerView with the new forecast data
                        updateRecyclerView(forecastResponse.list)
                    }
                }
                is Resource.Error -> {
                    // Handle error state if needed
                    setWeatherDataVisibility(false)
                    val errorMessage = resource.errorMessage
                    Log.e("DOH!", "Error fetching forecast data: $errorMessage")
                }
            }
        }
    }



    private fun fetchForecastData() {
        locationRepository.getCurrentLocation { latitude, longitude ->
            weatherDataHandler.fetchWeatherForecast(latitude, longitude,
                WeatherDataHandler.ForecastType.HOURLY
            )
        }
    }

    private fun setupRecyclerView() {
        // Create the adapter
        hourlyAdapter = HourlyAdapter()

        // Set up the RecyclerView with the adapter
        binding.rvHourlyForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hourlyAdapter
        }
    }

    private fun updateRecyclerView(hourlyList: List<WeatherItem>) {
        // Update the adapter's data with the new forecast list
        hourlyAdapter.submitList(hourlyList)

    }
    private fun setWeatherDataVisibility(isVisible: Boolean) {
        binding.clLoading.visibility = if (isVisible) View.INVISIBLE else View.VISIBLE
        binding.clTop.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.rvHourlyForecast.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.clTop.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun refreshHourlyWeatherData() {
        // Show the loading state (optional, if you want to indicate that data is being fetched)
        setWeatherDataVisibility(false)
//        binding.progressBar.visibility = View.VISIBLE

        // Request the weather data
        fetchForecastData()
    }

}




