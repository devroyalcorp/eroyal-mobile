package com.worka.eroyal.extensions

import android.location.Location
import com.worka.eroyal.data.base.LocationData
import com.worka.eroyal.data.tasks.Customer

fun LocationData.convertToLocation(): Location {
    val location = Location("")
    location.latitude = this.latitude
    location.longitude = this.longitude
    return location
}

fun Customer.getLocation(): Location {
    val location = Location("")
    this.latitude?.let { location.latitude = it.filterEmpty("0.0").toDouble() }
    this.longitude?.let { location.longitude = it.filterEmpty("0.0").toDouble() }
    return location
}
