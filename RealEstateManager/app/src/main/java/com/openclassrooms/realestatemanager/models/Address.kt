package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by galou on 2019-07-03
 */
@Entity
class Address (@PrimaryKey val id: Int, var address: String, var longitude: Double, var latitude: Double, var neighbourhood: String)