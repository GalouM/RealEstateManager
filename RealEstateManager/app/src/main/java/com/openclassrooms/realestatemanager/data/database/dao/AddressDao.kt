package com.openclassrooms.realestatemanager.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Address

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AddressDao {
    @Query("SELECT * FROM address WHERE address_id = :propertyId")
    suspend fun getAddress(propertyId: Int): List<Address>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAddress(address: Address): Long

    @Update
    suspend fun updateAddress(address: Address)


}