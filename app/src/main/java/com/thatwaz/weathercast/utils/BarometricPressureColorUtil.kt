package com.thatwaz.weathercast.utils

import android.graphics.Color

object BarometricPressureColorUtil {
//    private const val GOOD_PRESSURE_LOWER_BOUND = 980
//    private const val GOOD_PRESSURE_UPPER_BOUND = 1030
//    private const val BAD_PRESSURE_LOWER_BOUND = 950
//    private const val BAD_PRESSURE_UPPER_BOUND = 979

    // Update this constant
    private const val LOW_PRESSURE_THRESHOLD = 30.05

//    private fun isBarometricPressureGood(pressure: Int): Boolean {
//        return pressure in GOOD_PRESSURE_LOWER_BOUND..GOOD_PRESSURE_UPPER_BOUND
//    }

    fun getPressureColor(pressureInInHg: Int): Int {
        return if (pressureInInHg < LOW_PRESSURE_THRESHOLD) {
            Color.parseColor("#FF1744") // Red color for low pressure
        } else {
            Color.parseColor("#00C853") // Green color for good pressure
        }
    }

    // Implement the getPressureColor function
//    fun getPressureColor(pressure: Int): Int {
//        return if (isBarometricPressureGood(pressure)) {
//            Color.parseColor("#00C853") // Green color for good pressure
//        } else {
//            Color.parseColor("#FF1744") // Red color for bad pressure
//        }
//    }

//    private fun isBarometricPressureGood(pressure: Int): Boolean {
//        return pressure in GOOD_PRESSURE_LOWER_BOUND..GOOD_PRESSURE_UPPER_BOUND
//    }

//    fun getPressureColor(pressure: Int): Int {
//        return if (isBarometricPressureGood(pressure)) {
//            Color.parseColor("#00C853") // Green color for good pressure
//        } else {
//            Color.parseColor("#FF1744") // Red color for bad pressure
//        }
//    }
}
