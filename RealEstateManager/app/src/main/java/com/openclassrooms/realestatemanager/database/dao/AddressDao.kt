package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.realestatemanager.models.Address

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AddressDao {
    @Query("SELECT * FROM Address WHERE id = :addressId")
    fun getAddress(addressId: Int): LiveData<List<Address>>

    @Update
    suspend fun updateAddress(address: Address): Int

    @Query("DELETE FROM Address WHERE id = :addressId")
    fun deleteAddress(addressId: Int): Int

}