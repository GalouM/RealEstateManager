package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Address

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AddressDao {
    @Query("SELECT * FROM Address WHERE propertyId = :propertyId")
    fun getAddress(propertyId: Int): LiveData<List<Address>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAddress(address: Address): Long

    @Update
    suspend fun updateAddress(address: Address)


}