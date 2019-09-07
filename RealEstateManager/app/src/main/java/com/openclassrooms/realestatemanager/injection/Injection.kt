package com.openclassrooms.realestatemanager.injection

import android.content.Context
import com.openclassrooms.realestatemanager.data.api.GeocodingApiService
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.utils.KEY_PREF

/**
 * Created by galou on 2019-07-24
 */
class Injection {

    companion object {

        private fun providesAgentRepository(context: Context): AgentRepository {
            val database = REMDatabase.getDatabase(context)
            return AgentRepository.getAgentRepository(database.agentDao())
        }

        private fun providesPropertyRepository(context: Context): PropertyRepository {
            val database = REMDatabase.getDatabase(context)
            val geocodingApi = GeocodingApiService.create()
            return PropertyRepository.getPropertyRepository(database.propertyDao(), database.amenityDao(),
                    database.pictureDao(), database.addressDao(), geocodingApi)
        }

        private fun providesCurrencyRepository(context: Context): CurrencyRepository{
            val sharedPreferences = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE)
            return CurrencyRepository.getCurrencyRepository(sharedPreferences)
        }

        fun providesViewModelFactory(context: Context): ViewModelFactory {
            val agentRepository = providesAgentRepository(context)
            val propertyRepository = providesPropertyRepository(context)
            val currencyRepository = providesCurrencyRepository(context)
            return ViewModelFactory(agentRepository, propertyRepository, currencyRepository)
        }
    }
}