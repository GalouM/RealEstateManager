package com.openclassrooms.realestatemanager.utils.extensions

import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData

/**
 * Created by galou on 2019-09-27
 */

fun List<PropertyWithAllData>.keepPropertiesWithAllAmenityRequested(numberAmenities: Int): List<PropertyWithAllData>{
    val idPropertyToKeep = this
            .groupingBy { it.property.id }
            .eachCount()
            .filterValues { it == numberAmenities }.keys
    val propertyToDisplay = mutableListOf<PropertyWithAllData>()
    idPropertyToKeep.forEach { id ->
        this.find { it.property.id == id }?.let{ propertyToDisplay.add(it) }
    }

    return propertyToDisplay
}