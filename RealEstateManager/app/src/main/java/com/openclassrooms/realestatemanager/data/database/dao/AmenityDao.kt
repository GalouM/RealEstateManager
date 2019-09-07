package com.openclassrooms.realestatemanager.data.database.dao

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenity(amenities: List<Amenity>)

    @Query("DELETE FROM amenities WHERE property = :idProperty")
    suspend fun deleteAmenities(idProperty: Int)
}