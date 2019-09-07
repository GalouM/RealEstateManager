package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.entity.Amenity

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AmenityDao {
    @Query("SELECT * FROM amenities WHERE property = :propertyId")
    suspend fun getAmenities(propertyId: Int): List<Amenity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenity(amenities: List<Amenity>)

    @Query("DELETE FROM amenities WHERE property = :idProperty")
    suspend fun deleteAmenities(idProperty: Int)
}