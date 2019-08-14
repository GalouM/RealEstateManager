package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Property

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PropertyDao {
    @Query("SELECT * FROM Property")
    suspend fun getAllProperties(): List<Property>

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    suspend fun getProperty(propertyId: Int): List<Property>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createProperty(agent: Property): Long

    @Update
    suspend fun updateProperty(property: Property)
}