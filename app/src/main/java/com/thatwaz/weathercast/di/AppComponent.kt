package com.thatwaz.weathercast.di

import com.thatwaz.weathercast.view.ui.CurrentWeatherFragment
import com.thatwaz.weathercast.view.ui.HourlyFragment
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, NetworkModule::class])
interface AppComponent {

    // Inject into CurrentWeatherFragment
    fun inject(fragment: CurrentWeatherFragment)

    // Inject into WeatherViewModel
    fun inject(viewModel: WeatherViewModel)

    // Inject into HourlyFragment
    fun inject(fragment: HourlyFragment)

    // Add other inject methods for any other classes that need injection
}



//@Component(modules = [AppModule::class])
//interface AppComponent {
//
//    // Inject into CurrentWeatherFragment
//    fun inject(fragment: CurrentWeatherFragment)
//
//    // Inject into WeatherViewModel
//    fun inject(viewModel: WeatherViewModel)
//
//    // Inject into HourlyFragment
//    fun inject(fragment: HourlyFragment)
//
//    // Add other inject methods for any other classes that need injection
//}




//import com.thatwaz.weathercast.view.ui.CurrentWeatherFragment
//import com.thatwaz.weathercast.view.ui.HourlyFragment
//import com.thatwaz.weathercast.viewmodel.WeatherViewModel
//import dagger.Component
//
//@Component(modules = [AppModule::class])
//interface AppComponent {
//
//    // Inject into CurrentWeatherFragment
//    fun inject(fragment: CurrentWeatherFragment)
//
//    // Inject into WeatherViewModel
//    fun inject(viewModel: WeatherViewModel)
//
//    fun inject(fragment: HourlyFragment)
//
//    // Add other inject methods for any other classes that need injection
//}
