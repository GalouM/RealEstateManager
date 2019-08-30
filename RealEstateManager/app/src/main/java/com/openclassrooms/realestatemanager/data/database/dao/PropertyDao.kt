package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.utils.TypeProperty

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

    @Query("SELECT * FROM Property WHERE " +
            "(price BETWEEN :minPrice AND :maxPrice) " +
            "AND (surface BETWEEN :minSurface AND :maxSurface)" +
            "AND (rooms >= :minNbRoom)" +
            "AND (bedrooms >= :minNbBedrooms)" +
            "AND (bathrooms >= :minNbBathrooms)" +
            "AND (agent IN (:listAgents))" +
            "AND (type IN (:listTypes))")
    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double,
            minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<Int>,
            listTypes: List<TypeProperty>
    ): List<Property>
}