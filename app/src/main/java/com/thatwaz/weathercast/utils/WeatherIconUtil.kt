package com.thatwaz.weathercast.utils

import com.thatwaz.weathercast.R

object WeatherIconUtil {

    fun getWeatherImageResource(iconId: String): Int {
        return when (iconId) {
            "01d" -> R.drawable.img_clear_sky
            "02d" -> R.drawable.img_isolated_clouds
            "03d", "04d" -> R.drawable.img_partly_cloudy
            "09d", "10d", "13d" -> R.drawable.img_mostly_cloudy
            "11d" -> R.drawable.img_thunderstorm2
            "50d" -> R.drawable.img_mist
            "01n", "02n", "03n", "04n", "09n", "10n", "13n", "11n", "50n" -> R.drawable.img_night_clear
            else -> R.drawable.aaa_error_image_homer
        }
    }

//    fun convertNightToDayIcon(iconId: String): String {
//        return when (iconId) {
//            "01n" -> "01d" // Clear sky night to clear sky day
//            "02n", "03n", "04n", "50n" -> "02d" // Cloudy night to partly cloudy day
//            "09n" -> "09d" // Showers night to showers day
//            "10n" -> "10d" // Rain night to rain day
//            "11n" -> "11d" // Thunderstorm night to thunderstorm day
//            "13n" -> "13d" // Snow night to snow day
//            else -> iconId // Keep other icons unchanged
//        }
//    }

    fun getForecastWeatherIconResource(iconId: String): Int {
        return when (iconId) {
            "01n" -> R.drawable.day_clear_sky // Convert clear night to clear day
            "02n", "03n", "04n", "50n" -> R.drawable.day_partly_cloudy // Convert cloudy night to partly cloudy day
            "09n" -> R.drawable.day_showers // Convert showers night to showers day
            "10n" -> R.drawable.day_rain // Convert rain night to rain day
            "11n" -> R.drawable.day_thunderstorm // Convert thunderstorm night to thunderstorm day
            "13n" -> R.drawable.day_snow // Convert snow night to snow day
            else -> getWeatherIconResource(iconId) // Use existing mapping for other icons
        }
    }

    fun getWeatherIconResource(iconId: String): Int {
        return when (iconId) {
            "01d" -> R.drawable.day_clear_sky
            "02d", "03d", "04d", "50d" -> R.drawable.day_partly_cloudy
            "09d" -> R.drawable.day_showers
            "10d" -> R.drawable.day_rain
            "11d" -> R.drawable.day_thunderstorm
            "13d" -> R.drawable.day_snow
            "01n" -> R.drawable.night_clear
            "02n", "03n", "04n", "50n" -> R.drawable.night_cloudy
            "09n" -> R.drawable.night_showers
            "10n" -> R.drawable.night_rain
            "11n" -> R.drawable.night_thunderstorm
            "13n" -> R.drawable.night_snow
            else -> R.drawable.aaa_error_icon_doh
        }
    }
}

