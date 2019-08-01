package com.openclassrooms.realestatemanager.injection

import android.content.Context
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.PropertyRepository
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

        private fun providesPropertyRepository(context: Context): PropertyRepository{
            val database = REMDatabase.getDatabase(context)
            return PropertyRepository(database.propertyDao(), database.amenityDao(),
                    database.pictureDao(), database.addressDao())
        }

        fun providesViewModelFactory(context: Context): ViewModelFactory {
            val agentRepository = providesAgentRepository(context)
            val propertyRepository = providesPropertyRepository(context)
            return ViewModelFactory(agentRepository, propertyRepository)
        }
    }
}