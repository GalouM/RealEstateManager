package com.openclassrooms.realestatemanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.TypeAmenity

/**
 * Created by galou on 2019-07-03
 */
@Entity( tableName = "amenities",
        foreignKeys = [
    ForeignKey(
            entity = Property::class,
            parentColumns = ["property_id"],
            childColumns = ["property"],
            onDelete = ForeignKey.CASCADE
    )
        ]
)
data class Amenity (
        @ColumnInfo(name = "amenity_id") @PrimaryKey val id: String = "",
        val property: String = "",
        @ColumnInfo(name = "type_amenity")val type: TypeAmenity = TypeAmenity.BUSES
)