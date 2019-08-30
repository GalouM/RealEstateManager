package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PropertyDao {
    @Query("SELECT * FROM properties")
    suspend fun getAllProperties(): List<Property>

    @Query("SELECT * FROM properties WHERE property_id = :propertyId")
    suspend fun getProperty(propertyId: Int): List<Property>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createProperty(agent: Property): Long

    @Update
    suspend fun updateProperty(property: Property)

    @Query("SELECT * FROM properties WHERE " +
            "(price BETWEEN :minPrice AND :maxPrice) " +
            "AND (surface BETWEEN :minSurface AND :maxSurface) " +
            "AND (rooms >= :minNbRoom) " +
            "AND (bedrooms >= :minNbBedrooms OR bedrooms IS NULL) " +
            "AND (bathrooms >= :minNbBathrooms OR bathrooms IS NULL) " +
            "AND (agent IN (:listAgents)) " +
            "AND (type_property IN (:listTypes))")
    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double,
            minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<Int>, listTypes: List<TypeProperty>
    ): List<Property>

    @Query("SELECT * FROM properties INNER JOIN amenities ON amenities.property = properties.property_id WHERE amenities.type_amenity IN (:listAmenities)")
    suspend fun getPropertiesQueryWithAmenities(
            listAmenities: List<TypeAmenity>
    ): List<Property>
}