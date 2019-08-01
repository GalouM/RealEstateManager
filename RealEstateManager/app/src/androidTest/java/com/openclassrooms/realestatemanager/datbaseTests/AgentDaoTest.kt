package com.openclassrooms.realestatemanager.datbaseTests

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.data.entity.Agent
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
 * Created by galou on 2019-07-03
 */

@RunWith(AndroidJUnit4::class)
class AgentDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var agentDao: AgentDao
    private lateinit var db: REMDatabase
    private val agent1 = Agent(1, "Galou", "Minisini", "galou@rem.com", "+999-803-999", "http://mypictute")

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
    fun createAndGetAgent() = runBlocking {
        val id = agentDao.createAgent(agent1)
        val agentFromDB = agentDao.getAgent(id.toInt()).waitForValue()
        Log.d("id", id.toString())
        Log.d("agents", agentFromDB.toString())
        assertEquals(agentFromDB[0].id, id.toInt())
    }

    @Test
    @Throws(Exception::class)
    fun getAllAgents() = runBlocking{
        val agent2 = Agent(2,"New", "Agent", "agent2u@rem.com", "+999-999-999", "http://mypictute1")
        agentDao.createAgent(agent1)
        agentDao.createAgent(agent2)
        val allAgents = agentDao.getAllAgents().waitForValue()
        assertEquals(agent1.id, allAgents[0].id)
        assertEquals(agent2.id, allAgents[1].id)

    }
}
