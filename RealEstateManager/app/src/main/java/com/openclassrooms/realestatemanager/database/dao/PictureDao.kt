package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.models.Picture

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PictureDao {
    @Query("SELECT * FROM Amenity WHERE property = :propertyId")
    fun getPictures(propertyId: Int): LiveData<List<Picture>>

    @Insert
    suspend fun insertPicture(picture: Picture): Int

    @Query("DELETE FROM Amenity WHERE id = :pictureId")
    fun deletePicture(pictureId: Int): Int
}