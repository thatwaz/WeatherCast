package com.thatwaz.weathercast.model.weatherresponse


import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all")
    val all: Int
)