package com.openclassrooms.realestatemanager.datbaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.data.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.utils.TypeAmenity
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
class PropertyAndDataDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var propertyDao: PropertyDao
    private lateinit var agentDao: AgentDao
    private lateinit var db: REMDatabase
    private val agent1 = generateAgent()
    private val property1 = generateProperty(agent1.id)
    private val address1 = generateAddress(property1.id)
    private val amenity1 = generateAmenity(property1.id, TypeAmenity.SUBWAY)
    private val amenity2 = generateAmenity(property1.id, TypeAmenity.BUSES)
    private val amenitiesProperty1 = listOf(amenity1, amenity2)
    private val picture1 = generatePicture(property1.id, 1)
    private val picture2 = generatePicture(property1.id, 2)
    private val picturesProperty1 = listOf(picture1, picture2)

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, REMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        propertyDao = db.propertyDao()
        agentDao = db.agentDao()

        runBlocking {
            agentDao.createAgent(agent1)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun createAndGetPropertyWithData() = runBlocking {
        propertyDao.createPropertyAndData(property1, address1, picturesProperty1, amenitiesProperty1)
        val propertyFromDao = propertyDao.getProperty(property1.id)
        assertEquals(property1.id, propertyFromDao[0].property.id)
        assertEquals(address1.propertyId, propertyFromDao[0].address[0].propertyId)
        assertEquals(amenity1.id, propertyFromDao[0].amenities[0].id)
        assertEquals(amenity2.id, propertyFromDao[0].amenities[1].id)
        assertEquals(picture1.id, propertyFromDao[0].pictures[0].id)
        assertEquals(picture2.id, propertyFromDao[0].pictures[1].id)
    }

    @Test
    @Throws(Exception::class)
    fun updateAndGetProperty() = runBlocking {
        propertyDao.createProperty(property1)
        property1.price = 600000.00
        propertyDao.updateProperty(property1)
        val propertyFromDao = propertyDao.getProperty(property1.id)
        assertEquals(property1.id, propertyFromDao[0].property.id)
        assertEquals(property1.price, propertyFromDao[0].property.price)
    }

    @Test
    @Throws(Exception::class)
    fun getAllProperties() = runBlocking {
        val property2 = generatePropertyWithNoPicture(agent1.id)
        val addressProperty2 = generateAddress(property2.id)
        val listProperties = listOf(property1, property2)
        val listAddress = listOf(address1, addressProperty2)
        propertyDao.createPropertiesAndData(listProperties, listAddress, picturesProperty1, amenitiesProperty1)
        val propertyFromDao = propertyDao.getAllProperties()
        assertEquals(property1.id, propertyFromDao[0].property.id)
        assertEquals(property2.id, propertyFromDao[1].property.id)
    }
}