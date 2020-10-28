package com.worka.eroyal.data.base

import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("latitude") var latitude: Double,
    @SerializedName("longitude") var longitude: Double
)