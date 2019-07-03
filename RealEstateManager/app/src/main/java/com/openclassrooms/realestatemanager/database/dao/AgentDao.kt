package com.openclassrooms.realestatemanager.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.models.Agent

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AgentDao {
    @Query("SELECT * FROM Agent")
    fun getAllAgents(): LiveData<List<Agent>>

    @Query("SELECT * FROM Agent WHERE id = :agentId")
    fun getAgent(agentId: Int): LiveData<List<Agent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAgent(agent: Agent)
}