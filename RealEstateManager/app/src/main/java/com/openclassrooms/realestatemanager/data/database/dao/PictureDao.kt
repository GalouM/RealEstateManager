package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.entity.Picture

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface PictureDao {
    @Query("SELECT * FROM Picture WHERE property = :propertyId")
    fun getPictures(propertyId: Int): LiveData<List<Picture>>

    @Insert
    suspend fun insertPicture(picture: Picture)

    @Query("DELETE FROM Picture WHERE url = :urlPicture")
    suspend fun deletePicture(urlPicture: String)
}