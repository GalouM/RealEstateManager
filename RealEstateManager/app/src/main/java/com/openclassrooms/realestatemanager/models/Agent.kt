package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by galou on 2019-07-03
 */
@Entity
data class Agent(@PrimaryKey(autoGenerate = true) val id: Int, val firstName: String,
            val lastName: String, val email: String,
            val phoneNumber: String, val urlProfilePicture: String)