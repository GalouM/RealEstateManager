package com.openclassrooms.realestatemanager.datbaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.TypeAmenity
import com.openclassrooms.realestatemanager.TypeProperty
import com.openclassrooms.realestatemanager.database.REMDatabase
import com.openclassrooms.realestatemanager.database.dao.AddressDao
import com.openclassrooms.realestatemanager.database.dao.AgentDao
import com.openclassrooms.realestatemanager.database.dao.AmenityDao
import com.openclassrooms.realestatemanager.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.models.Address
import com.openclassrooms.realestatemanager.models.Agent
import com.openclassrooms.realestatemanager.models.Amenity
import com.openclassrooms.realestatemanager.models.Property
import com.openclassrooms.realestatemanager.waitForValue
import junit.framework.Assert
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
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
class AmenityDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var amenityDao: AmenityDao
    private lateinit var propertyDao: PropertyDao
    private lateinit var agentDao: AgentDao
    private lateinit var addressDao: AddressDao
    private lateinit var db: REMDatabase
    private val property1 = Property(1, TypeProperty.HOUSE, 500000.00, 150.00, 3,
            2, 1, null, 1, "10/10/2018", false, null, 1)
    private val address1 = Address(1, "12 rue de nulle part", -13.0987, 544.3454, "Olympic Village")
    private val agent1 = Agent(1, "Galou", "Minisini", "galou@rem.com", "+999-803-999", "http://mypictute")
    private val amenity1 = Amenity(1, 1, TypeAmenity.PARK)

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, REMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        propertyDao = db.propertyDao()
        agentDao = db.agentDao()
        addressDao = db.addressDao()
        amenityDao = db.amenityDao()

        runBlocking {
            agentDao.createAgent(agent1)
            addressDao.createAddress(address1)
            propertyDao.createProperty(property1)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAmenity() = runBlocking {
        amenityDao.insertAmenity(amenity1)
        val amenityFromDao = amenityDao.getAmenities(property1.id).waitForValue()
        assertEquals(amenityFromDao[0].id, amenity1.id)

    }

    @Test
    @Throws(Exception::class)
    fun deleteAmenity() = runBlocking {
        amenityDao.insertAmenity(amenity1)
        amenityDao.deleteAmenity(amenity1.id)
        val amenityFromDao = amenityDao.getAmenities(property1.id).waitForValue()
        assertTrue(amenityFromDao.isEmpty())

    }
}