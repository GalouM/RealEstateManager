package com.openclassrooms.realestatemanager.data.database.dao

import android.database.Cursor
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.utils.PICTURE_TABLE_NAME

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PictureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(pictures: List<Picture>)

    @Query("DELETE FROM $PICTURE_TABLE_NAME WHERE picture_id IN (:pictureId)")
    suspend fun deletePictures(pictureId: List<String>)

    @Update
    suspend fun updatePicture(pictures: List<Picture>)


    @Query("SELECT * FROM $PICTURE_TABLE_NAME WHERE picture_id = :pictureId")
    fun getPictureWithCursor(pictureId: String): Cursor

    @Query("SELECT * FROM $PICTURE_TABLE_NAME WHERE id_property = :propertyId")
    fun getPropertyPicturesWithCursor(propertyId: String): Cursor

}