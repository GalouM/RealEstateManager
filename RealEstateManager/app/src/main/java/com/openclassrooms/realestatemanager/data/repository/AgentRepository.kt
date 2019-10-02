package com.openclassrooms.realestatemanager.data.repository

import androidx.core.net.toUri
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.openclassrooms.realestatemanager.data.database.dao.AgentDao
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.utils.AGENT_COLLECTION
import com.openclassrooms.realestatemanager.utils.STORAGE_PATH_AGENT_PICTURE
import java.util.*

/**
 * Created by galou on 2019-07-05
 */
class AgentRepository (private val agentDao: AgentDao) {

    suspend fun createAgent(agent: Agent): Task<Void> {
        createAgentInLocalDB(agent)
        return createAgentInNetwork(agent)
    }

    //-----------------
    // LOCAL REQUEST
    //-----------------

    suspend fun getAllAgents(): List<Agent> {
        return agentDao.getAllAgents()
    }

    suspend fun getAgent(agentId: String): List<Agent> {
        return agentDao.getAgent(agentId)
    }

    private suspend fun createAgentInLocalDB(agent: Agent) {
        agentDao.createAgent(agent)
    }

    suspend fun createAllNewAgents(agents: List<Agent>){
        agentDao.createAgents(agents)
    }

    //-----------------
    // NETWORK REQUEST
    //-----------------

    private val dbNetwork = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val agentCollection = dbNetwork.collection(AGENT_COLLECTION)

    private fun createAgentInNetwork(agent: Agent): Task<Void> = agentCollection.document(agent.id).set(agent)

    fun uploadAgentPhotoInNetwork(urlPhoto: String, idAgent: String): UploadTask = getReferenceAgentPicture(idAgent)
            .putFile(urlPhoto.toUri())

    fun getAgentsFromNetwork(latestUpdate: Date?): Task<QuerySnapshot> {
        return if (latestUpdate != null) {
            agentCollection
                    .whereGreaterThanOrEqualTo("creationDate", latestUpdate)
                    .get()
        } else {
            agentCollection.get()
        }

    }

    fun getReferenceAgentPicture(idAgent: String) = storage.reference
            .child("${STORAGE_PATH_AGENT_PICTURE}$idAgent")



    companion object{
        @Volatile
        private var INSTANCE: AgentRepository? = null
        fun getAgentRepository(agentDao: AgentDao): AgentRepository {
            return INSTANCE
                    ?: synchronized(this){
                        val instance = AgentRepository(agentDao)
                        INSTANCE = instance
                        return instance
                    }
        }
    }
}