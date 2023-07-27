package com.thatwaz.weathercast.util

import android.icu.text.SimpleDateFormat
import java.util.*

object ConversionUtility {
    fun kelvinToFahrenheit(kelvinTemp: Double): Int {
        return ((kelvinTemp - 273.15) * 9 / 5 + 32).toInt()
    }

    fun hPaToInHg(hPaPressure: Int): String {
        return (hPaPressure / 33.8639).toString()
    }

    fun convertMetersToMiles(visibilityInMeters: Int): Double {
        return visibilityInMeters / 1609.34 // 1 mile = 1609.34 meters
    }

    fun convertUnixTimestampToTime(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(unixTimestamp * 1000)
        return dateFormat.format(date)
    }

    fun getWindDirection(degrees: Int): String {
        val directions = arrayOf(
            "N", "NNE", "NE", "ENE",
            "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW",
            "W", "WNW", "NW", "NNW"
        )

        val index = ((degrees + 11.25) / 22.5).toInt() % 16
        return directions[index]
    }


}