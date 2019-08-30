package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.entity.Amenity

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AmenityDao {
    @Query("SELECT * FROM amenities WHERE property = :propertyId")
    suspend fun getAmenities(propertyId: Int): List<Amenity>

    @Insert
    suspend fun insertAmenity(amenities: Amenity)

    @Query("DELETE FROM amenities WHERE amenity_id = :amenityId")
    suspend fun deleteAmenity(amenityId: Int)
}