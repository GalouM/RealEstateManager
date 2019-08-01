package com.openclassrooms.realestatemanager.datbaseTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.database.dao.AddressDao
import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.data.database.dao.PictureDao
import com.openclassrooms.realestatemanager.data.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.utils.TypeProperty
import com.openclassrooms.realestatemanager.waitForValue
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
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
class PictureDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var pictureDao: PictureDao
    private lateinit var propertyDao: PropertyDao
    private lateinit var agentDao: AgentDao
    private lateinit var addressDao: AddressDao
    private lateinit var db: REMDatabase
    private val property1 = Property(1, TypeProperty.HOUSE, 500000.00, 150.00, 3,
            2, 1, null,  "10/10/2018", false, null, 1)
    private val address1 = Address(1, "12 rue de nulle part", -13.0987, 544.3454, "Olympic Village")
    private val agent1 = Agent(1, "Galou", "Minisini", "galou@rem.com", "+999-803-999", "http://mypictute")
    private val picture1 = Picture("http://pictureUrl", 1, "My picture")

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, REMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        propertyDao = db.propertyDao()
        agentDao = db.agentDao()
        addressDao = db.addressDao()
        pictureDao = db.pictureDao()

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
    fun insertAndGetPictures() = runBlocking {
        pictureDao.insertPicture(picture1)
        val pictureFromDao = pictureDao.getPictures(property1.id!!).waitForValue()
        assertEquals(pictureFromDao[0].url, picture1.url)

    }

    @Test
    @Throws(Exception::class)
    fun deletePicture() = runBlocking {
        pictureDao.insertPicture(picture1)
        pictureDao.deletePicture(picture1.url)
        val pictureFromDao = pictureDao.getPictures(property1.id!!).waitForValue()
        assertTrue(pictureFromDao.isEmpty())
    }
}