package com.thatwaz.weathercast.util

import android.graphics.Color

object BarometricPressureColorUtility {
    private const val GOOD_PRESSURE_LOWER_BOUND = 980
    private const val GOOD_PRESSURE_UPPER_BOUND = 1030
    private const val BAD_PRESSURE_LOWER_BOUND = 950
    private const val BAD_PRESSURE_UPPER_BOUND = 979

    private fun isBarometricPressureGood(pressure: Int): Boolean {
        return pressure in GOOD_PRESSURE_LOWER_BOUND..GOOD_PRESSURE_UPPER_BOUND
    }

    fun getPressureColor(pressure: Int): Int {
        return if (isBarometricPressureGood(pressure)) {
            Color.parseColor("#00C853") // Green color for good pressure
        } else {
            Color.parseColor("#FF1744") // Red color for bad pressure
        }
    }
}
