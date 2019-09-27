package com.openclassrooms.realestatemanager.utils

import android.util.Log
import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData
import java.util.*

/**
 * Created by galou on 2019-09-10
 */

fun displayData(message: String){
    Log.e("REMData", message)
}

var idGenerated: String = ""
    get() {
        field = UUID.randomUUID().toString()
        return field
    }

var todaysDate: Date = Calendar.getInstance(Locale.CANADA).time

fun keepPropertyWithAllAmenityRequested(properties: List<PropertyWithAllData>, numberAmenities: Int): List<PropertyWithAllData>{
    val idPropertyToKeep = properties
            .groupingBy { it.property.id }
            .eachCount()
            .filterValues { it == numberAmenities }.keys
    val propertyToDisplay = mutableListOf<PropertyWithAllData>()
    idPropertyToKeep.forEach { id ->
        properties.find { it.property.id == id }?.let{ propertyToDisplay.add(it) }
    }

    return propertyToDisplay
}