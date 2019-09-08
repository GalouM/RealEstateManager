package com.openclassrooms.realestatemanager.utils.extensions

import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.utils.TypeAmenity

/**
 * Created by galou on 2019-09-07
 */

fun Amenity.toDrawable(): Int{
    return when(this.type){
        TypeAmenity.SCHOOL -> R.drawable.school_icon
        TypeAmenity.PLAYGROUND -> R.drawable.playground_icon
        TypeAmenity.SHOP -> R.drawable.shopping_icon
        TypeAmenity.BUSES -> R.drawable.bus_icon
        TypeAmenity.SUBWAY -> R.drawable.subway_icon
        TypeAmenity.PARK -> R.drawable.park_icon
    }
}