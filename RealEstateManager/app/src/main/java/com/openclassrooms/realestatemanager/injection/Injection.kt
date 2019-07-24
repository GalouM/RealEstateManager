package com.openclassrooms.realestatemanager.injection

import android.content.Context
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.database.REMDatabase

/**
 * Created by galou on 2019-07-24
 */
class Injection {

    companion object {

        private fun providesAgentRepository(context: Context): AgentRepository{
            val database = REMDatabase.getDatabase(context)
            return AgentRepository(database.agentDao())
        }

        fun providesViewModelFactory(context: Context): ViewModelFactory {
            val agentRepository = providesAgentRepository(context)
            return ViewModelFactory(agentRepository)
        }
    }
}