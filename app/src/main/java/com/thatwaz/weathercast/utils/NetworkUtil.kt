package com.thatwaz.weathercast.utils


import android.net.ConnectivityManager
import android.net.NetworkCapabilities


object NetworkUtil {
    fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {
        val networkCapabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)

        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }
}


//object NetworkUtil {
//    fun isInternetAvailable(connectivityManager: ConnectivityManager): Boolean {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val networkCapabilities = connectivityManager
//                .getNetworkCapabilities(connectivityManager.activeNetwork)
//            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
//                ?: false
//        } else {
//            // For devices with API level < 23 (M)
//            @Suppress("DEPRECATION")
//            val networkInfo = connectivityManager.activeNetworkInfo
//            networkInfo?.isConnected ?: false
//        }
//    }
//}
