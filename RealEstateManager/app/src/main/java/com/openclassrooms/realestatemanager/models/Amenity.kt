package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.TypeAmenity

/**
 * Created by galou on 2019-07-03
 */
@Entity(foreignKeys = [ForeignKey(entity = Property::class,
        parentColumns = ["id"],
        childColumns = ["property"])])
class Amenity (@PrimaryKey val id: Int, val property: Int, val type: TypeAmenity)