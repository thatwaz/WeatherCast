package com.thatwaz.weathercast.model.forecastresponse


import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("pod")
    val pod: String
)