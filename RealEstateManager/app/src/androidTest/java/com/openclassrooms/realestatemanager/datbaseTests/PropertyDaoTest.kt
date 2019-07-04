package com.openclassrooms.realestatemanager.datbaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.TypeProperty
import com.openclassrooms.realestatemanager.database.REMDatabase
import com.openclassrooms.realestatemanager.database.dao.AddressDao
import com.openclassrooms.realestatemanager.database.dao.AgentDao
import com.openclassrooms.realestatemanager.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.models.Address
import com.openclassrooms.realestatemanager.models.Agent
import com.openclassrooms.realestatemanager.models.Property
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
class PropertyDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var propertyDao: PropertyDao
    private lateinit var agentDao: AgentDao
    private lateinit var addressDao: AddressDao
    private lateinit var db: REMDatabase
    private val property1 = Property(1, TypeProperty.HOUSE, 500000.00, 150.00, 3,
            2, 1, null, 1, "10/10/2018", false, null, 1)
    private val address1 = Address(1, "12 rue de nulle part", -13.0987, 544.3454, "Olympic Village")
    private val agent1 = Agent(1, "Galou", "Minisini", "galou@rem.com", "+999-803-999", "http://mypictute")

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, REMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        propertyDao = db.propertyDao()
        agentDao = db.agentDao()
        addressDao = db.addressDao()

        runBlocking {
            agentDao.createAgent(agent1)
            addressDao.createAddress(address1)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetProperty() = runBlocking {
        propertyDao.createProperty(property1)
        val propertyFromDao = propertyDao.getProperty(property1.id).waitForValue()
        assertEquals(property1.id, propertyFromDao[0].id)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetProperty() = runBlocking {
        propertyDao.createProperty(property1)
        property1.price = 600000.00
        propertyDao.updateProperty(property1)
        val propertyFromDao = propertyDao.getProperty(property1.id).waitForValue()
        assertEquals(property1.id, propertyFromDao[0].id)
        assertEquals(property1.price, propertyFromDao[0].price)
    }

    @Test
    @Throws(Exception::class)
    fun getAllProperties() = runBlocking {
        propertyDao.createProperty(property1)
        val property2 = Property(2, TypeProperty.FLAT, 800000.00, 200.00, 6,
                4, 2, null, 1, "10/10/2019", false, null, 1)
        propertyDao.createProperty(property2)
        val propertyFromDao = propertyDao.getAllProperties().waitForValue()
        assertEquals(property1.id, propertyFromDao[0].id)
        assertEquals(property2.id, propertyFromDao[1].id)
    }
}