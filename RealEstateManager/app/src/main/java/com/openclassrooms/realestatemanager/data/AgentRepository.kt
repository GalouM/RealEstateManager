package com.openclassrooms.realestatemanager.data

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.data.entity.Agent

/**
 * Created by galou on 2019-07-05
 */
class AgentRepository (private val agentDao: AgentDao){

    suspend fun getAllAgents(): List<Agent> {
        return agentDao.getAllAgents()
    }

    fun getAgent(agentId: Int): List<Agent> {
        return agentDao.getAgent(agentId)
    }

    suspend fun createAgent(agent: Agent){
        agentDao.createAgent(agent)
    }
}