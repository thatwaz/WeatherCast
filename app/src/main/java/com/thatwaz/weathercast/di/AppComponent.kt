package com.thatwaz.weathercast.di

import com.thatwaz.weathercast.view.ui.CurrentWeatherFragment
import com.thatwaz.weathercast.view.ui.ForecastFragment
import com.thatwaz.weathercast.view.ui.HourlyFragment
import com.thatwaz.weathercast.viewmodel.WeatherViewModel
import dagger.Component

@Component(modules = [AppModule::class, DatabaseModule::class, NetworkModule::class])
interface AppComponent {

    fun inject(fragment: CurrentWeatherFragment)

    fun inject(viewModel: WeatherViewModel)

    fun inject(fragment: HourlyFragment)

    fun inject(fragment: ForecastFragment)

}

