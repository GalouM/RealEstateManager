package com.openclassrooms.realestatemanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by galou on 2019-07-03
 */
@Entity(foreignKeys = [
    ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["propertyId"],
            onDelete = ForeignKey.CASCADE)])
data class Address (@PrimaryKey val propertyId: Int, var street: String,
                    var city: String, var postalCode: String,
                    var country: String, var state: String,
                    var longitude: Double, var latitude: Double,
                    var neighbourhood: String, var mapIconUrl: String)
