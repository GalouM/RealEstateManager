package com.openclassrooms.realestatemanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Created by galou on 2019-07-03
 */
@Entity(tableName = "agents")
data class Agent(
        @PrimaryKey
        @ColumnInfo(name = "agent_id")val id: String,
        @ColumnInfo(name = "first_name")val firstName: String,
        @ColumnInfo(name = "last_name")val lastName: String,
        val email: String,
        @ColumnInfo(name = "phone_number")val phoneNumber: String,
        @ColumnInfo(name = "url_picture")var urlProfilePicture: String?,
        @ColumnInfo(name = "creation_date") val creationDate: Date
)