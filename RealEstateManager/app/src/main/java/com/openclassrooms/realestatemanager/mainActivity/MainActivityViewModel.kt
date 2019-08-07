package com.openclassrooms.realestatemanager.mainActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-07-04
 */
class MainActivityViewModel(
        private val agentRepository: AgentRepository
) : BaseViewModel(){

    private val viewStateLD = MutableLiveData<MainActivityViewState>()
    val viewState: LiveData<MainActivityViewState>
        get() = viewStateLD
    private var currentViewState = MainActivityViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private var searchAgentsJob: Job? = null

     fun actionFromIntent(intent: MainActivityIntent){
        when(intent){
            is MainActivityIntent.OpenAddPropertyActivityIntent -> onOpenAddPropertyRequest()
            else -> throw Exception("No Intent found")
        }

    }

     private fun resultToViewState(result: Lce<MainActivityResult>){
        currentViewState = when (result){
            is Lce.Content -> {
                when(result.packet){
                    is MainActivityResult.OpenAddPropertyResult ->{
                        currentViewState.copy(isOpenAddProperty = true,
                                errorSource = null,
                                isLoading = false)
                    }
                }
            }

            is Lce.Loading -> {
                currentViewState.copy(isLoading = true)
            }
            is Lce.Error -> {
                when(result.packet){
                    is MainActivityResult.OpenAddPropertyResult ->{
                        currentViewState.copy(
                                isOpenAddProperty = false,
                                errorSource = ErrorSource.NO_AGENT_IN_DB,
                                isLoading = false)
                    }
                }
            }
        }
    }

    private fun onOpenAddPropertyRequest(){
        resultToViewState(Lce.Loading())
        if(searchAgentsJob?.isActive == true) searchAgentsJob?.cancel()

        searchAgentsJob = launch {
            val agents: List<Agent>? = agentRepository.getAllAgents()
            val result: Lce<MainActivityResult> = if(agents == null || agents.isEmpty()){
                Lce.Error(MainActivityResult.OpenAddPropertyResult)
            } else{
                Lce.Content(MainActivityResult.OpenAddPropertyResult)
            }
            resultToViewState(result)
        }

    }

}

