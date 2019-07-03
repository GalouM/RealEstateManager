package com.openclassrooms.realestatemanager.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by galou on 2019-07-03
 */
@Entity(foreignKeys = [ForeignKey(entity = Property::class,
        parentColumns = ["id"],
        childColumns = ["property"])])
class Picture(@PrimaryKey val url: Int, val property: Int, val description: String?)