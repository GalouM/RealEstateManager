package com.openclassrooms.realestatemanager.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.openclassrooms.realestatemanager.data.entity.Address

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAddress(address: Address): Long

    @Update
    suspend fun updateAddress(address: Address)


}