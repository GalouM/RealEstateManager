package com.openclassrooms.realestatemanager.data.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.utils.AMENITY_TABLE_NAME
import com.openclassrooms.realestatemanager.utils.PICTURE_TABLE_NAME

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AmenityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAmenity(amenities: List<Amenity>)

    @Query("DELETE FROM $AMENITY_TABLE_NAME WHERE amenity_id IN (:amenitiesId)")
    suspend fun deleteAmenities(amenitiesId: List<String>)


    @Query("SELECT * FROM $AMENITY_TABLE_NAME WHERE amenity_id = :amenityId")
    fun getAmenityWithCursor(amenityId: String): Cursor

    @Query("SELECT * FROM $AMENITY_TABLE_NAME WHERE property = :propertyId")
    fun getPropertyAmenitiesWithCursor(propertyId: String): Cursor
}