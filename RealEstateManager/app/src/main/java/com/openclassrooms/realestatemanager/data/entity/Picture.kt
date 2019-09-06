package com.openclassrooms.realestatemanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by galou on 2019-07-03
 */
@Entity(
        tableName = "pictures",
        foreignKeys = [
    ForeignKey(
            entity = Property::class,
            parentColumns = ["property_id"],
            childColumns = ["property"],
            onDelete = ForeignKey.CASCADE
    )
        ]
)
data class Picture(
        @PrimaryKey val url: String,
        @ColumnInfo(name = "thumbnail_url")val thumbnailUrl: String?,
        @ColumnInfo(name = "server_url")val serverUrl: String?,
        val property: Int,
        val description: String?
)