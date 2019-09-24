package com.openclassrooms.realestatemanager.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.openclassrooms.realestatemanager.utils.AGENT_TABLE_NAME
import java.util.*

/**
 * Created by galou on 2019-07-03
 */
@Entity(tableName = AGENT_TABLE_NAME)
data class Agent(
        @PrimaryKey
        @ColumnInfo(name = "agent_id")var id: String = "",
        @ColumnInfo(name = "first_name")var firstName: String = "",
        @ColumnInfo(name = "last_name")var lastName: String = "",
        var email: String = "",
        @ColumnInfo(name = "phone_number")var phoneNumber: String = "",
        @ColumnInfo(name = "url_picture")var urlProfilePicture: String? = null,
        @ColumnInfo(name = "creation_date") var creationDate: Date = Date()
)
