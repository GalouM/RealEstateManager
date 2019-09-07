package com.openclassrooms.realestatemanager.data.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Created by galou on 2019-09-06
 */
data class PropertyWithAllData(
        @Embedded val property: Property,
        @Relation(parentColumn = "property_id", entityColumn = "address_id", entity = Address::class)
        val address: List<Address>,
        @Relation(parentColumn = "property_id", entityColumn = "id_property", entity = Picture::class)
        val pictures: List<Picture>,
        @Relation(parentColumn = "property_id", entityColumn = "property", entity = Amenity::class)
        val amenities: List<Amenity>
)