package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.data.entity.Agent
import io.reactivex.Observable
import javax.inject.Inject

/**
 * Created by galou on 2019-07-05
 */
class AgentRepository @Inject constructor(private val agentDao: AgentDao) : AgentLocalDataSource{
    override fun getAllAgents(): Observable<List<Agent>> {
        return agentDao.getAllAgents()
    }

    override fun getAgent(agentId: Int): Observable<List<Agent>> {
        return agentDao.getAgent(agentId)
    }

    override suspend fun createAgent(agent: Agent) {
        agentDao.createAgent(agent)
    }
}