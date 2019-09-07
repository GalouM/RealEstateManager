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
    @Query("SELECT * FROM pictures WHERE property = :propertyId")
    suspend fun getPictures(propertyId: Int): List<Picture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(pictures: Picture)

    @Query("DELETE FROM pictures WHERE url IN (:urlPictures)")
    suspend fun deletePictures(urlPictures: List<Int>)
}