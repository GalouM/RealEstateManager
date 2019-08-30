package com.openclassrooms.realestatemanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by galou on 2019-07-03
 */
@Entity(tableName = "agents")
data class Agent(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "agent_id")val id: Int?,
        @ColumnInfo(name = "first_name")val firstName: String,
        @ColumnInfo(name = "last_name")val lastName: String,
        val email: String,
        @ColumnInfo(name = "phone_number")val phoneNumber: String,
        @ColumnInfo(name = "url_picture")val urlProfilePicture: String?
)