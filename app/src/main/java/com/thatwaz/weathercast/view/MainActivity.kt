package com.thatwaz.weathercast.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.thatwaz.weathercast.R
import com.thatwaz.weathercast.databinding.ActivityMainBinding
import com.thatwaz.weathercast.model.application.WeatherCastApplication
import com.thatwaz.weathercast.model.data.WeatherDataHandler
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val menuProviders = mutableListOf<MenuProvider>()

    @Inject
    lateinit var weatherDataHandler: WeatherDataHandler

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        (application as WeatherCastApplication).appComponent.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.bnvWeatherCast.setOnItemSelectedListener { item ->
            val destinationId = item.itemId

            if (destinationId == navController.currentDestination?.id) {
                return@setOnItemSelectedListener true
            }

            when (destinationId) {
                R.id.hourlyFragment -> {
                    val action = R.id.action_shared_to_hourlyFragment
                    navController.navigate(action)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
                R.id.currentWeatherFragment -> {
                    val action = R.id.action_shared_to_currentWeatherFragment
                    navController.navigate(action)

                }
                R.id.forecastFragment -> {
                    val action = R.id.action_shared_to_forecastFragment
                    navController.navigate(action)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                }
            }
            true
        }
    }


    override fun addMenuProvider(provider: MenuProvider, owner: LifecycleOwner) {
        menuProviders.add(provider)
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                menuProviders.remove(provider)
            }
        })
    }
    override fun removeMenuProvider(provider: MenuProvider) {
        menuProviders.remove(provider)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        menuProviders.forEach {
            it.onCreateMenu(menu!!, inflater)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return menuProviders.any { it.onMenuItemSelected(item) } || super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        menuProviders.clear()
    }

}







