package com.thatwaz.weathercast.view.ui

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentForecastBinding
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.data.LocationRepository
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import com.thatwaz.weathercast.model.forecastresponse.DailyForecast
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

    private var forecastAdapter: ForecastAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        (activity?.application as WeatherCastApplication).appComponent.inject(this)
        _binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_refresh, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_refresh -> {
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

        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)

        setupRecyclerView()
        fetchForecastData()


        viewModel.forecastData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    setWeatherDataVisibility(false)
                }
                is Resource.Success -> {
                    val dailyForecasts = resource.data
                    if (dailyForecasts != null) {
                        setWeatherDataVisibility(true)
                        binding.tvForecastLocation.text = dailyForecasts[0].cityName
                        updateRecyclerView(dailyForecasts)
                    }
                }
                is Resource.Error -> {
                    setWeatherDataVisibility(false)
                    val errorMessage = resource.errorMessage ?: "An error occurred"
                    displayErrorMessage(errorMessage)
                }
            }
        }
    }

    private fun displayErrorMessage(errorMessage: String) {
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    private fun fetchForecastData() {
        weatherDataHandler.fetchWeatherForecast(
            WeatherDataHandler.ForecastType.DAILY
        )
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = forecastAdapter
        }
    }

    private fun updateRecyclerView(dailyForecasts: List<DailyForecast>) {
        forecastAdapter?.submitList(dailyForecasts)
    }
    private fun setWeatherDataVisibility(isVisible: Boolean) {
        binding.clLoading.visibility = if (isVisible) View.INVISIBLE else View.VISIBLE
        binding.clForecastTop.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.rvForecast.visibility = if (isVisible) View.VISIBLE else View.GONE

    }
    private fun refreshForecastWeatherData() {
        setWeatherDataVisibility(false)
        fetchForecastData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        forecastAdapter = null
        _binding = null
    }
}