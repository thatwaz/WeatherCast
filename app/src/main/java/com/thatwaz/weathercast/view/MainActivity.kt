package com.thatwaz.weathercast.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.ActivityMainBinding

//forecast    https://api.openweathermap.org/data/2.5/forecast?lat=44.34&lon=10.99&appid=2f73f1f2102337b8e6e433e1747d6b4b
//google api key = AIzaSyCgmAtIPSA1RfsFFk532h0Sl9tald37AIk
//https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&hourly=temperature_2m,relativehumidity_2m,apparent_temperature,precipitation_probability,precipitation,rain,showers,snowfall,weathercode,cloudcover_low,cloudcover_mid,cloudcover_high,windgusts_10m&daily=weathercode,sunrise,sunset,windspeed_10m_max,windgusts_10m_max&temperature_unit=fahrenheit&windspeed_unit=mph&precipitation_unit=inch&timezone=America%2FNew_York
// start date 07/10/2023
// Base completion date 08/14/2023


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bnvWeatherCast.setOnItemSelectedListener { item ->
            val destinationId = item.itemId

            if (destinationId == navController.currentDestination?.id) {
                return@setOnItemSelectedListener true
            }

            when (destinationId) {
                R.id.hourlyFragment -> {
                    val action = R.id.action_shared_to_hourlyFragment
                    navController.navigate(action)
                }
                R.id.currentWeatherFragment -> {
                    val action = R.id.action_shared_to_currentWeatherFragment
                    navController.navigate(action)
                }
                R.id.forecastFragment -> {
                    val action = R.id.action_shared_to_forecastFragment
                    navController.navigate(action)
                }
            }
            true
        }
    }
}





