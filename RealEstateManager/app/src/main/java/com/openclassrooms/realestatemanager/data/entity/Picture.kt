package com.openclassrooms.realestatemanager.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Created by galou on 2019-07-03
 */
@Entity(foreignKeys = [
    ForeignKey(
            entity = Property::class,
            parentColumns = ["id"],
            childColumns = ["property"],
            onDelete = ForeignKey.CASCADE)])
data class Picture(@PrimaryKey val url: String, val property: Int, val description: String?)