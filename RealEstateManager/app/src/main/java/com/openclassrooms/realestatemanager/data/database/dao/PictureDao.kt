package com.openclassrooms.realestatemanager.data.database.dao

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(pictures: List<Picture>): List<Long>

    @Query("DELETE FROM pictures WHERE id_property = :propertyId")
    suspend fun deletePictures(propertyId: String)
}