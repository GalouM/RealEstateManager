package com.openclassrooms.realestatemanager.data.database.dao

import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Picture

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(pictures: List<Picture>): List<Long>

    @Query("DELETE FROM pictures WHERE picture_id IN (:pictureId)")
    suspend fun deletePictures(pictureId: List<String>)

    @Update
    suspend fun updatePicture(pictures: List<Picture>)
}