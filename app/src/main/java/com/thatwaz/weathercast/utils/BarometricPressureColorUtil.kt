package com.thatwaz.weathercast.utils

import android.graphics.Color


object BarometricPressureColorUtil {

    private const val LOW_PRESSURE_THRESHOLD_HPA =
        (28.54 * 33.86389).toInt() // Sets air pressure t.v. to red if it's in air pressure is low


    fun getPressureColor(pressureInHpa: Int): Int {
        return if (pressureInHpa < LOW_PRESSURE_THRESHOLD_HPA) {
            Color.parseColor("#FF1744") // Red color for low pressure
        } else {
            Color.parseColor("#00C853") // Green color for good pressure
        }
    }
}


