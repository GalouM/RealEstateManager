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
            childColumns = ["id_property"],
            onDelete = ForeignKey.CASCADE
    )
        ]
)
data class Picture(
        @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "picture_id") val id: Int?,
        val url: String,
        @ColumnInfo(name = "thumbnail_url") var thumbnailUrl: String?,
        @ColumnInfo(name = "server_url") var serverUrl: String?,
        @ColumnInfo(name = "id_property") val property: Int?,
        var description: String?,
        @ColumnInfo(name = "is_main_picture") var isMainPicture: Boolean
)