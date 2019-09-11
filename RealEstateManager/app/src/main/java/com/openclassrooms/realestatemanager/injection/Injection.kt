package com.openclassrooms.realestatemanager.injection

import android.content.Context
import com.openclassrooms.realestatemanager.data.api.GeocodingApiService
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.repository.SaveDataRepository

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

        private fun providesCurrencyRepository(context: Context) = CurrencyRepository.getCurrencyRepository(context)


        private fun providesSaveDataRepository(context: Context) = SaveDataRepository.getSaveDataRepository(context)


        fun providesViewModelFactory(context: Context): ViewModelFactory {
            val agentRepository = providesAgentRepository(context)
            val propertyRepository = providesPropertyRepository(context)
            val currencyRepository = providesCurrencyRepository(context)
            val saveDataRepository = providesSaveDataRepository(context)
            return ViewModelFactory(agentRepository, propertyRepository, currencyRepository, saveDataRepository)
        }
    }
}