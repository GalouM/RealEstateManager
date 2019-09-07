package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.entity.Picture

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PictureDao {
    @Query("SELECT * FROM pictures WHERE id_property = :propertyId ORDER BY picture_id")
    suspend fun getPictures(propertyId: Int): List<Picture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(pictures: List<Picture>)

    @Query("DELETE FROM pictures WHERE id_property = :propertyId")
    suspend fun deletePictures(propertyId: Int)
}