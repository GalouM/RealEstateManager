package com.openclassrooms.realestatemanager.datbaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.database.dao.AddressDao
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.waitForValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

/**
 * Created by galou on 2019-07-04
 */
@RunWith(AndroidJUnit4::class)
class AddressDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var addressDao: AddressDao
    private lateinit var db: REMDatabase
    private val address = Address(1, "12 rue de nulle part", -13.0987, 544.3454, "Olympic Village")

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, REMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        addressDao = db.addressDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetAddress() = runBlocking {
        addressDao.createAddress(address)
        val addressFromDao = addressDao.getAddress(address.id).waitForValue()
        assertEquals(address.id, addressFromDao[0].id)
    }

    @Test
    @Throws(Exception::class)
    fun updateAddress() = runBlocking {
        addressDao.createAddress(address)
        address.address = "15 route de nulle part"
        addressDao.updateAddress(address)
        val addressFromDao = addressDao.getAddress(address.id).waitForValue()
        assertEquals(address.id, addressFromDao[0].id)
        assertEquals(address.address, addressFromDao[0].address)
    }
}