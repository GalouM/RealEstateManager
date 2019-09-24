package com.openclassrooms.realestatemanager.data.database.dao

import android.database.Cursor
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.utils.ADDRESS_TABLE_NAME

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAddress(address: Address)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAddresses(addresses: List<Address>)

    @Update
    suspend fun updateAddress(address: Address)

    @Query("SELECT * FROM $ADDRESS_TABLE_NAME WHERE address_id = :addressId")
    fun getAddressWithCursor(addressId: String): Cursor


}