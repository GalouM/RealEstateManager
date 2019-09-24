package com.openclassrooms.realestatemanager.data.entity

import androidx.room.*
import com.openclassrooms.realestatemanager.utils.AMENITY_TABLE_NAME
import com.openclassrooms.realestatemanager.utils.TypeAmenity

/**
 * Created by galou on 2019-07-03
 */
@Entity( tableName = AMENITY_TABLE_NAME,
        indices = [Index(value = ["property", "type_amenity"], unique = true)],
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
        @ColumnInfo(name = "amenity_id") @PrimaryKey var id: String = "",
        var property: String = "",
        @ColumnInfo(name = "type_amenity")var type: TypeAmenity = TypeAmenity.BUSES
)