package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.models.Property

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PropertyDao {
    @Query("SELECT * FROM Property")
    fun getAllProperties(): LiveData<List<Property>>

    @Query("SELECT * FROM Property WHERE id = :propertyId")
    fun getProperty(propertyId: Int): LiveData<MutableList<Property>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createProperty(agent: Property)

    @Update
    suspend fun updateProperty(property: Property)
}