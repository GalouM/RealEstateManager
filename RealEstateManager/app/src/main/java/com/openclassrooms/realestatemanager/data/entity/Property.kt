package com.openclassrooms.realestatemanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.TypeProperty
import java.util.*

/**
 * Created by galou on 2019-07-03
 */
@Entity(
        tableName = "properties",
        foreignKeys = [
    ForeignKey(
            entity = Agent::class,
            parentColumns = ["agent_id"],
            childColumns = ["agent"],
            onDelete = ForeignKey.NO_ACTION)])
data class Property (
        @ColumnInfo(name = "property_id") @PrimaryKey val id: String,
        @ColumnInfo(name = "type_property")var type: TypeProperty,
        var price: Double,
        var surface: Double,
        var rooms: Int,
        var bedrooms: Int?,
        var bathrooms: Int?,
        var description: String?,
        @ColumnInfo(name = "on_market_since") var onMarketSince: Date,
        var sold: Boolean,
        @ColumnInfo(name = "sell_date") var sellDate: Date?,
        var agent: String,
        @ColumnInfo(name = "has_picture") var hasPictures: Boolean,
        @ColumnInfo(name = "creation_date") val creationDate: Date
)

