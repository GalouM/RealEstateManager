package com.openclassrooms.realestatemanager.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.addAgent.AddAgentViewModel
import com.openclassrooms.realestatemanager.addProperty.AddPropertyViewModel
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.PropertyRepository
import com.openclassrooms.realestatemanager.mainActivity.MainActivityViewModel

/**
 * Created by galou on 2019-07-09
 */
class ViewModelFactory(
        private val agentRepository: AgentRepository,
        private val propertyRepository: PropertyRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> MainActivityViewModel(agentRepository) as T
            modelClass.isAssignableFrom(AddAgentViewModel::class.java) -> AddAgentViewModel(agentRepository) as T
            modelClass.isAssignableFrom(AddPropertyViewModel::class.java) -> AddPropertyViewModel(agentRepository, propertyRepository) as T
            else -> throw Exception("Unknown ViewModel class")

        }

    }
}
