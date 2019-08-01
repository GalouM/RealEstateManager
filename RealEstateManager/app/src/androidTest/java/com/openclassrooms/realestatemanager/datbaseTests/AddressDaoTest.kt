package com.openclassrooms.realestatemanager.datbaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.database.dao.AddressDao
import com.openclassrooms.realestatemanager.data.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.utils.TypeProperty
import com.openclassrooms.realestatemanager.waitForValue
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by galou on 2019-07-04
 */
@RunWith(AndroidJUnit4::class)
class AddressDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var addressDao: AddressDao
    private lateinit var propertyDao: PropertyDao
    private lateinit var db: REMDatabase
    private val property1 = Property(1, TypeProperty.HOUSE, 500000.00, 150.00, 3,
            2, 1, null,  "10/10/2018", false, null, 1)
    private val address = Address(property1.id!!, "12 rue de nulle part", -13.0987, 544.3454, "Olympic Village")

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
        val addressFromDao = addressDao.getAddress(address.propertyId).waitForValue()
        assertEquals(address.propertyId, addressFromDao[0].propertyId)
    }

    @Test
    @Throws(Exception::class)
    fun updateAddress() = runBlocking {
        addressDao.createAddress(address)
        address.address = "15 route de nulle part"
        addressDao.updateAddress(address)
        val addressFromDao = addressDao.getAddress(address.propertyId).waitForValue()
        assertEquals(address.propertyId, addressFromDao[0].propertyId)
        assertEquals(address.address, addressFromDao[0].address)
    }
}