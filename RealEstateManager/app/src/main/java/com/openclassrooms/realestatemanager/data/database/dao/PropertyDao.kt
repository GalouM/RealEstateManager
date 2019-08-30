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

    @Query("SELECT * FROM properties " +
            "INNER JOIN address ON address.address_id = properties.property_id WHERE " +
            "address.neighbourhood LIKE :neighborhood " +
            "AND (properties.price BETWEEN :minPrice AND :maxPrice) " +
            "AND (properties.surface BETWEEN :minSurface AND :maxSurface) " +
            "AND (properties.rooms >= :minNbRoom) " +
            "AND (properties.bedrooms >= :minNbBedrooms OR bedrooms IS NULL) " +
            "AND (properties.bathrooms >= :minNbBathrooms OR bathrooms IS NULL) " +
            "AND (properties.agent IN (:listAgents)) " +
            "AND (properties.type_property IN (:listTypes)) " +
            "AND (properties.sold IN (:isSold))")
    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double,
            minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<Int>, listTypes: List<TypeProperty>, neighborhood: String, isSold: List<Int>
    ): List<Property>

    @Query("SELECT * FROM properties " +
            "INNER JOIN amenities ON amenities.property = properties.property_id " +
            "INNER JOIN address ON address.address_id = properties.property_id WHERE " +
            "(amenities.type_amenity IN (:listAmenities)) " +
            "AND (address.neighbourhood LIKE :neighborhood) " +
            "AND (properties.price BETWEEN :minPrice AND :maxPrice)" +
            "AND (properties.surface BETWEEN :minSurface AND :maxSurface) " +
            "AND (properties.rooms >= :minNbRoom) " +
            "AND (properties.bedrooms >= :minNbBedrooms OR bedrooms IS NULL) " +
            "AND (properties.bathrooms >= :minNbBathrooms OR bathrooms IS NULL) " +
            "AND (properties.agent IN (:listAgents)) " +
            "AND (properties.type_property IN (:listTypes)) " +
            "AND (properties.sold IN (:isSold))")
    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double,
            minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<Int>, listTypes: List<TypeProperty>, neighborhood: String, isSold: List<Int>,
            listAmenities: List<TypeAmenity>
    ): List<Property>
}