package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.data.entity.Agent
import io.reactivex.Observable

/**
 * Created by galou on 2019-07-05
 */
interface AgentLocalDataSource : AgentDao{
    override fun getAllAgents(): Observable<List<Agent>>

    override fun getAgent(agentId: Int): Observable<List<Agent>>

    override suspend fun createAgent(agent: Agent)
}