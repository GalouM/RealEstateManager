package com.openclassrooms.realestatemanager.utils.extensions

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import java.math.RoundingMode
import kotlin.math.*

/**
 * Created by galou on 2019-08-16
 */

fun LatLng.toBounds(radius: Double): LatLngBounds{
    val distanceFromCenter = radius * sqrt(2.0)
    val southWest = computeOffsetBounds(this, distanceFromCenter, 225.0)
    val northEast = computeOffsetBounds(this, distanceFromCenter, 45.0)
    return LatLngBounds.Builder().include(northEast).include(southWest).build()
}

private fun computeOffsetBounds(from: LatLng, distanceSet: Double, headingSet: Double): LatLng{
    val distance = distanceSet / 6371009.0
    val heading = Math.toRadians(headingSet)
    val fromLat = Math.toRadians(from.latitude)
    val fromLng = Math.toRadians(from.longitude)
    val cosDistance = cos(distance)
    val sinDistance = sin(distance)
    val sinFromLat = sin(fromLat)
    val cosFromLat = cos(fromLat)
    val sinLat = cosDistance * sinFromLat + sinDistance * cosFromLat * cos(heading)
    val dLng = atan2(sinDistance * cosFromLat * sin(heading), cosDistance - sinFromLat * sinLat)
    return LatLng(Math.toDegrees(asin(sinLat)), Math.toDegrees(fromLng + dLng))
}

fun LatLng.isEqualTo(previousLocation: Location, roundTo: Int): Boolean{
    val locationLatRoundUp = this.latitude.toBigDecimal().setScale(roundTo, RoundingMode.UP)
    val locationLngRoundUp = this.longitude.toBigDecimal().setScale(roundTo, RoundingMode.UP)
    val previousLatRoundUp = previousLocation.latitude.toBigDecimal().setScale(roundTo, RoundingMode.UP)
    val previousLngRoundUp = previousLocation.longitude.toBigDecimal().setScale(roundTo, RoundingMode.UP)

    return locationLatRoundUp == previousLatRoundUp && locationLngRoundUp == previousLngRoundUp

}