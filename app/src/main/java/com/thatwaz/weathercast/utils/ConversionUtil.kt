package com.thatwaz.weathercast.utils

import android.icu.text.SimpleDateFormat
import java.util.*

object ConversionUtil {
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

    fun breakTextIntoLines(text: String, maxCharsPerLine: Int): String {
        val builder = StringBuilder()
        var startIndex = 0

        while (startIndex < text.length) {
            val endIndex = startIndex + maxCharsPerLine

            // If endIndex goes beyond the length of the text, break the loop
            if (endIndex >= text.length) {
                builder.append(text.substring(startIndex))
                break
            }

            // Find the last space character within the limit
            var lastSpaceIndex = text.lastIndexOf(' ', endIndex)

            // If no space found within the limit, break at maxCharsPerLine
            if (lastSpaceIndex == -1 || lastSpaceIndex < startIndex) {
                lastSpaceIndex = endIndex - 1
            }

            builder.append(text.substring(startIndex, lastSpaceIndex + 1))
            builder.append('\n')

            // Update the startIndex for the next iteration
            startIndex = lastSpaceIndex + 1
        }

        return builder.toString()
    }

    fun String.capitalizeWords(): String = split(" ")
        .joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }



}
