package com.openclassrooms.realestatemanager.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.addAgent.AddAgentViewModel
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.mainActivity.MainActivityViewModel
import java.lang.Exception

/**
 * Created by galou on 2019-07-09
 */
class ViewModelFactory(
        private val agentRepository: AgentRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when{
            modelClass.isAssignableFrom(MainActivityViewModel::class.java) -> return MainActivityViewModel(agentRepository) as T
            modelClass.isAssignableFrom(AddAgentViewModel::class.java) -> return AddAgentViewModel(agentRepository) as T
            else -> throw Exception("Unknown ViewModel class")

        }

    }
}
