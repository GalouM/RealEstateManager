package com.openclassrooms.realestatemanager.data.entity

import android.content.ContentValues
import androidx.room.*
import com.openclassrooms.realestatemanager.data.database.Converters
import com.openclassrooms.realestatemanager.utils.PROPERTY_TABLE_NAME
import com.openclassrooms.realestatemanager.utils.TypeProperty
import com.openclassrooms.realestatemanager.utils.extensions.toDate
import com.openclassrooms.realestatemanager.utils.todaysDate
import java.util.*

/**
 * Created by galou on 2019-07-03
 */
@Entity(
        tableName = PROPERTY_TABLE_NAME,
        foreignKeys = [
    ForeignKey(
            entity = Agent::class,
            parentColumns = ["agent_id"],
            childColumns = ["agent"],
            onDelete = ForeignKey.NO_ACTION)])
data class Property (
        @ColumnInfo(name = "property_id") @PrimaryKey var id: String = "",
        @ColumnInfo(name = "type_property")var type: TypeProperty = TypeProperty.HOUSE,
        var price: Double = 0.0,
        var surface: Double = 0.0,
        var rooms: Int = 0,
        var bedrooms: Int? = null,
        var bathrooms: Int? = null,
        var description: String? = null,
        @ColumnInfo(name = "on_market_since") var onMarketSince: Date = Date(),
        var sold: Boolean = false,
        @ColumnInfo(name = "sell_date") var sellDate: Date? = null,
        var agent: String = "",
        @ColumnInfo(name = "has_picture") var hasPictures: Boolean = false,
        @ColumnInfo(name = "creation_date") var creationDate: Date = Date()
)




