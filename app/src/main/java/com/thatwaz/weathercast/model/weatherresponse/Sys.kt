package com.thatwaz.weathercast.model.weatherresponse


import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("pod")
    val pod: String
)