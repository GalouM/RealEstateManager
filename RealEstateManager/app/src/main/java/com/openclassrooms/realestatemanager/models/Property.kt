package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.TypeProperty

/**
 * Created by galou on 2019-07-03
 */
@Entity(foreignKeys = [ForeignKey(entity = Address::class,
        parentColumns = ["id"],
        childColumns = ["address"]),
    ForeignKey(entity = Agent::class,
            parentColumns = ["id"],
            childColumns = ["agent"])])
class Property (@PrimaryKey val id: Int, var type: TypeProperty,
                var price: Double, var surface: Double,
                var rooms: Int, var bedrooms: Int?,
                var bathrooms: Int?, var description: String?,
                var address: Int, var onMarketSince: String,
                var sold: Boolean, var sellDate: String?,
                var agent: Int)