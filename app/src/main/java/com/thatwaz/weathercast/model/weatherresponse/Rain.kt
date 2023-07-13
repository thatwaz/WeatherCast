package com.thatwaz.weathercast.model.weatherresponse


import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    val h: Double
)