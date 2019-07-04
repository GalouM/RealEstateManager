package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.models.Amenity

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AmenityDao {
    @Query("SELECT * FROM Amenity WHERE property = :propertyId")
    fun getAmenities(propertyId: Int): LiveData<List<Amenity>>

    @Insert
    suspend fun insertAmenity(amenity: Amenity)

    @Query("DELETE FROM Amenity WHERE id = :amenityId")
    suspend fun deleteAmenity(amenityId: Int)
}