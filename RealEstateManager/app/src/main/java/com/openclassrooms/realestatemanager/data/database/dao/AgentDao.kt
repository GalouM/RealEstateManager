package com.openclassrooms.realestatemanager.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.entity.Agent

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AgentDao {
    @Query("SELECT * FROM agents")
    suspend fun getAllAgents(): List<Agent>

    @Query("SELECT * FROM agents WHERE agent_id = :agentId")
    suspend fun getAgent(agentId: Int): List<Agent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAgent(agent: Agent)
}