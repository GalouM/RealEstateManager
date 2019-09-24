package com.openclassrooms.realestatemanager.data.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.utils.AGENT_TABLE_NAME

/**
 * Created by galou on 2019-07-03
 */

@Dao
interface AgentDao {
    @Query("SELECT * FROM $AGENT_TABLE_NAME")
    suspend fun getAllAgents(): List<Agent>

    @Query("SELECT * FROM $AGENT_TABLE_NAME WHERE agent_id = :agentId")
    suspend fun getAgent(agentId: String): List<Agent>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAgent(agent: Agent): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createAgents(agents: List<Agent>)


    @Query("SELECT * FROM $AGENT_TABLE_NAME WHERE agent_id = :agentId")
    fun getAgentWithCursor(agentId: String): Cursor

    @Query("SELECT * FROM $AGENT_TABLE_NAME")
    fun getAllAgentsWithCursor(): Cursor
}