package com.openclassrooms.realestatemanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.TypeProperty

/**
 * Created by galou on 2019-07-03
 */
@Entity(foreignKeys = [
    ForeignKey(
            entity = Agent::class,
            parentColumns = ["id"],
            childColumns = ["agent"],
            onDelete = ForeignKey.NO_ACTION)])
data class Property (@PrimaryKey(autoGenerate = true) val id: Int?, var type: TypeProperty,
                     var price: Double, var surface: Double,
                     var rooms: Int, var bedrooms: Int?,
                     var bathrooms: Int?, var description: String?,
                     var onMarketSince: String, var sold: Boolean,
                     var sellDate: String?, var agent: Int)