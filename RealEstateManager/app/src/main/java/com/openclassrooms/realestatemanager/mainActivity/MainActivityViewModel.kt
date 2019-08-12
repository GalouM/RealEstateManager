package com.openclassrooms.realestatemanager.mainActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.CurrencyRepository
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-07-04
 */
class MainActivityViewModel(
        private val agentRepository: AgentRepository,
        private val currencyRepository: CurrencyRepository
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
            is MainActivityIntent.ChangeCurrencyIntent -> changeCurrency()
            is MainActivityIntent.GetCurrentCurrencyIntent -> emitCurrentCurrency()
        }

    }

     private fun resultToViewState(result: Lce<MainActivityResult>){
        currentViewState = when (result){
            is Lce.Content -> {
                when(result.packet){
                    is MainActivityResult.OpenAddPropertyResult ->{
                        currentViewState.copy(
                                isOpenAddProperty = true,
                                errorSource = null,
                                isLoading = false)
                    }
                    is MainActivityResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                isOpenAddProperty = false,
                                currency = result.packet.currency
                                )
                    }
                }
            }

            is Lce.Loading -> {
                currentViewState.copy(
                        isLoading = true,
                        errorSource = null,
                        isOpenAddProperty = false)
            }
            is Lce.Error -> {
                when(result.packet){
                    is MainActivityResult.OpenAddPropertyResult ->{
                        currentViewState.copy(
                                isOpenAddProperty = false,
                                errorSource = ErrorSource.NO_AGENT_IN_DB,
                                isLoading = false)
                    }
                    is MainActivityResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                isOpenAddProperty = false
                        )
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

    private fun changeCurrency(){
        currencyRepository.setCurrency()
        emitCurrentCurrency()

    }

    private fun emitCurrentCurrency(){
        val currency = currencyRepository.getCurrentCurrency()
        val result: Lce<MainActivityResult> = Lce.Content(MainActivityResult.ChangeCurrencyResult(currency))

        resultToViewState(result)
    }

}

