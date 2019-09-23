package com.openclassrooms.realestatemanager.data.entity

import android.content.ContentValues
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.ADDRESS_TABLE_NAME

/**
 * Created by galou on 2019-07-03
 */
@Entity(
        tableName = ADDRESS_TABLE_NAME,
        foreignKeys = [
    ForeignKey(
            entity = Property::class,
            parentColumns = ["property_id"],
            childColumns = ["address_id"],
            onDelete = ForeignKey.CASCADE
    )]
)
data class Address (@PrimaryKey @ColumnInfo(name = "address_id") var propertyId: String = "",
                    var street: String = "",
                    var city: String = "",
                    @ColumnInfo(name = "postal_code") var postalCode: String = "",
                    var country: String = "",
                    @ColumnInfo(name = "address_state") var state: String = "",
                    var longitude: Double = 0.0,
                    var latitude: Double = 0.0,
                    var neighbourhood: String = "",
                    @ColumnInfo(name = "map_icon_url") var mapIconUrl: String = "",
                    @ColumnInfo(name = "address_for_display") var addressForDisplay: String = ""
)


