package com.thatwaz.weathercast.utils

import android.graphics.Color
import android.util.Log

object BarometricPressureColorUtil {

    //    private const val LOW_PRESSURE_THRESHOLD_HPA = 1009 // This is equivalent to 30.05 inHg 28.54
    private const val LOW_PRESSURE_THRESHOLD_HPA =
        (28.54 * 33.86389).toInt() // Rounded to the nearest integer


    fun getPressureColor(pressureInHpa: Int): Int {
        Log.i("THRESHOLD","Thrwshod is $LOW_PRESSURE_THRESHOLD_HPA")
        return if (pressureInHpa < LOW_PRESSURE_THRESHOLD_HPA) {
            Color.parseColor("#FF1744") // Red color for low pressure
        } else {
            Color.parseColor("#00C853") // Green color for good pressure
        }
    }
}


//    fun getPressureColor(pressureInInHg: Int): Int {
//        return if (pressureInInHg < LOW_PRESSURE_THRESHOLD) {
//            Color.parseColor("#FF1744") // Red color for low pressure
//        } else {
//            Color.parseColor("#00C853") // Green color for good pressure
//        }
//    }

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

