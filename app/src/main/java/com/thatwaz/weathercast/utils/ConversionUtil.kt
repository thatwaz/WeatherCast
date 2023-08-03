package com.thatwaz.weathercast.utils

import android.icu.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


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

    fun convertUnixTimestampToTimeWithAMPM(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(unixTimestamp * 1000)
        return dateFormat.format(date)
    }

    fun convertUnixTimestampToTimeRange(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("h a", Locale.getDefault())
        val startTime = Date(unixTimestamp * 1000)
        val endTime = Date((unixTimestamp + 3 * 60 * 60) * 1000) // Adding 3 hours to the start time
        return "${dateFormat.format(startTime)} - ${dateFormat.format(endTime)}"
    }

    fun convertUnixTimestampToDate(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
        val date = Date(unixTimestamp * 1000) // Convert to milliseconds
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


    fun convertGMTtoLocal(gmtTime: Long): String {
        val instant = Instant.ofEpochSecond(gmtTime)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return localDateTime.format(dateFormat)
    }

    fun String.capitalizeWords(): String = split(" ")
        .joinToString(" ") {
            it.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }

}
