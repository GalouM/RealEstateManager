package com.openclassrooms.realestatemanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.TypeAmenity

/**
 * Created by galou on 2019-07-03
 */
@Entity(foreignKeys = [
    ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["property"],
            onDelete = ForeignKey.CASCADE)])
data class Amenity (@PrimaryKey(autoGenerate = true) val id: Int?, val property: Int, val type: TypeAmenity)