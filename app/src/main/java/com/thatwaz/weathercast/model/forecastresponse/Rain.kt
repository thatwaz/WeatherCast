package com.thatwaz.weathercast.model.forecastresponse


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    val h: Double
)