package com.thatwaz.weathercast.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import com.thatwaz.weathercast.model.forecastresponse.RainForecast
import java.util.*
import kotlin.math.round


object ConversionUtil {
    fun kelvinToFahrenheit(kelvinTemp: Double): Int {
        return ((kelvinTemp - 273.15) * 9 / 5 + 32).toInt()
    }

    fun hPaToInHg(hPaPressure: Int): String {
        return (hPaPressure / 33.8639).toString()
    }


    fun convertMetersToMiles(visibilityInMeters: Int): Int {
        return if (visibilityInMeters >= 10000) {
            10 // Optimal visibility in miles
        } else {
            val miles = visibilityInMeters / 1609.34 // Convert other values
            round(miles).toInt() // Round to the nearest Int
        }
    }

    // Converts a Unix timestamp to time using the device's default timezone.
    fun convertUnixTimestampToTimeWithAMPM(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(unixTimestamp * 1000)
        return dateFormat.format(date)
    }

   /*  Converts a Unix timestamp to time adjusted by the provided timezone offset (in seconds).
 Use this when needing timezone adjustments different from the device's default. It was especially
 helpful when mocking the location to get correct sunrise/sunset times.
 Make sure to update sunrise and sunset time in Current Weather Fragment accordingly. */

//    fun convertUnixTimestampToTimeWithAMPM(
//        unixTimestamp: Long,
//        timezoneOffsetInSeconds: Int
//    ): String {
//        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
//        dateFormat.timeZone = getTimeZone("GMT")  // Initially set to GMT
//
//        // Adjusting for the provided timezone offset
//        val adjustedDate = Date((unixTimestamp + timezoneOffsetInSeconds) * 1000)
//        return dateFormat.format(adjustedDate)
//    }

    fun convertUnixTimestampToTimeRange(unixTimestamp: Long): String {
        val dateFormat = SimpleDateFormat("h a", Locale.getDefault())
        val startTime = Date(unixTimestamp * 1000)
        val endTime = Date((unixTimestamp + 3 * 60 * 60) * 1000) /* Adding 3 hours to the start
        time removes current weather details in hourly fragment and displays only future data
        */
        return "${dateFormat.format(startTime)} TO ${dateFormat.format(endTime)}"
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


    fun convertRainToPercentage(rainForecast: RainForecast?): Int {
        if (rainForecast == null) {
            return 0 // If rain data is not available, return 0% chance of rain
        }

        // Maximum rainfall value used as reference (adjust as needed)
        val maxRainValue = 5.0 // For example, 5.0 mm is considered heavy rain

        // Get the rainfall amount for a 3-hour period
        val rain3h = rainForecast.`3h`

        // Calculate the percentage of rain relative to the maximum value
        val percentage = (rain3h / maxRainValue) * 100

        // Cap the percentage at 100% if it exceeds
        val cappedPercentage = if (percentage > 100) 100 else percentage

        // Round the percentage to the nearest integer
        val roundedPercentage = cappedPercentage.toInt()

        // Set a minimum threshold to make the percentage more meaningful
        val minThreshold = 1

        // If the rounded percentage is below the threshold, return 10%
        if (roundedPercentage < minThreshold) {
            return 10
        }
        // Calculate the nearest multiple of 10
        return ((roundedPercentage + 9) / 10) * 10
    }

    fun convertUnixTimestampToFormattedDate(date: Date): String {
        val dateFormat = SimpleDateFormat("E MMM d", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun convertUnixTimestampToRelativeDate(unixTimestamp: Long): String {
        val date = Date(unixTimestamp * 1000)
        val currentDate = Date(System.currentTimeMillis())

        return when {
            isSameDate(date, currentDate) -> "Today"
            isNextDay(date, currentDate) -> "Tomorrow"
            else -> {
                val dateFormat = SimpleDateFormat("E MMM d", Locale.getDefault())
                dateFormat.format(date)
            }
        }
    }

    private fun isSameDate(date1: Date, date2: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date1) == dateFormat.format(date2)
    }

    private fun isNextDay(date: Date, currentDate: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date
        cal2.time = currentDate
        cal2.add(Calendar.DAY_OF_YEAR, 1)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun String.capitalizeWords(): String = split(" ")
        .joinToString(" ") { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
}
