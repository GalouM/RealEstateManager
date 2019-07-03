package com.openclassrooms.realestatemanager

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.internal.runner.junit4.AndroidJUnit4Builder
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.database.REMDatabase
import com.openclassrooms.realestatemanager.database.dao.AgentDao
import com.openclassrooms.realestatemanager.models.Agent
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.lang.Exception

/**
 * Created by galou on 2019-07-03
 */

@RunWith(AndroidJUnit4::class)

class AgentDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var agentDao: AgentDao
    private lateinit var db: REMDatabase

    @Before
    fun createDatabase(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, REMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        agentDao = db.agentDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    suspend fun insertAndGetAgent() {
        val agent = Agent(1, "Galou", "Minisini", "galou@rem.com", "+999-803-999", "http://mypictute")
        agentDao.createAgent(agent)
        val agentFromDB = LiveDataTestUtil.getValue(agentDao.getAgent(agent.id))
        assertEquals(agentFromDB[0].id, agent.id)
    }
}
