package com.thatwaz.weathercast.utils.error

sealed class WeatherError {
    data class NetworkError(val message: String) : WeatherError()
    data class LocationError(val message: String) : WeatherError()
    data class DataParsingError(val message: String) : WeatherError()
    data class UnknownError(val message: String) : WeatherError()
}
