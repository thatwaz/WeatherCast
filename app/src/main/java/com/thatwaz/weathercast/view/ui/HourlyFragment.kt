package com.thatwaz.weathercast.view.ui

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.FragmentHourlyBinding
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import com.thatwaz.weathercast.model.forecastresponse.WeatherItem
import com.thatwaz.weathercast.utils.error.Resource
import com.thatwaz.weathercast.view.ui.adapters.HourlyAdapter
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import javax.inject.Inject


class HourlyFragment : Fragment() {

    @Inject
    lateinit var viewModel: WeatherViewModel

    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var weatherDataHandler: WeatherDataHandler
    private var _binding: FragmentHourlyBinding? = null
    private val binding get() = _binding!!
    private var hourlyAdapter: HourlyAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bottomNavView = activity?.findViewById(R.id.bnv_weather_cast) ?: return binding.root
        (activity?.application as WeatherCastApplication).appComponent.inject(this)
        _binding = FragmentHourlyBinding.inflate(inflater, container, false)
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
                        refreshHourlyWeatherData()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        weatherDataHandler = WeatherDataHandler(requireContext(), viewModel)

        fetchHourlyForecastData()
        setupRecyclerView()

        viewModel.hourlyData.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    setWeatherDataVisibility(false)
                }
                is Resource.Success -> {
                    val hourlyForecastResponse = resource.data
                    if (hourlyForecastResponse != null) {
                        setWeatherDataVisibility(true)
                        binding.tvHourlyLocation.text = hourlyForecastResponse.city.name
                        updateRecyclerView(hourlyForecastResponse.list)
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

    private fun fetchHourlyForecastData() {
            weatherDataHandler.fetchWeatherForecast(
                WeatherDataHandler.ForecastType.HOURLY
            )
        }

    private fun setupRecyclerView() {
        hourlyAdapter = HourlyAdapter()
        binding.rvHourlyForecast.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = hourlyAdapter
        }
    }

    private fun updateRecyclerView(hourlyList: List<WeatherItem>) {
        hourlyAdapter?.submitList(hourlyList)

    }
    private fun setWeatherDataVisibility(isVisible: Boolean) {
        binding.clLoading.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.clTop.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.rvHourlyForecast.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.clTop.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
    private fun refreshHourlyWeatherData() {
        setWeatherDataVisibility(false)
        viewModel.refreshHourlyWeatherData()
        fetchHourlyForecastData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.hourlyData.removeObservers(viewLifecycleOwner)
        weatherDataHandler.cleanUp()
        hourlyAdapter = null
        _binding = null

    }
}




