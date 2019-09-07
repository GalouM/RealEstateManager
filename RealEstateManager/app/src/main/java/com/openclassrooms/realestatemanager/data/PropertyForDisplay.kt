package com.openclassrooms.realestatemanager.data

import androidx.room.Embedded
import androidx.room.Relation
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property

/**
 * Created by galou on 2019-09-06
 */
class PropertyForDisplay(
        @Embedded val property: Property,
        @Relation(parentColumn = "property_id", entityColumn = "address_id", entity = Address::class) val address: Address,
        @Relation(parentColumn = "property_id", entityColumn = "property", entity = Picture::class) val pictures: List<Picture>
)