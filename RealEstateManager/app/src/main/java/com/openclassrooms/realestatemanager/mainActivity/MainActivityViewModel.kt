package com.openclassrooms.realestatemanager.mainActivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.mviBase.MviViewModel
import com.openclassrooms.realestatemanager.utils.notOfType
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject

/**
 * Created by galou on 2019-07-04
 */
class MainActivityViewModel(
        private val agentRepository: AgentRepository
) : ViewModel(){

    private val viewStateLD = MutableLiveData<MainActivityViewState>()
    val viewState: LiveData<MainActivityViewState>
        get() = viewStateLD
    private var currentViewState = MainActivityViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    fun actionFromIntent(intent: MainActivityIntent){
        when(intent){
            is MainActivityIntent.OpenAddPropertyActivityIntent -> {
                Log.e("tag", "intent received")
                onOpenAddPropertyRequest()}
            else -> throw Exception("No Intent found")
        }

    }

    private fun onOpenAddPropertyRequest(){
        val agents: List<Agent>? = agentRepository.getAllAgents().value
        if (agents != null && agents.isNotEmpty()) {
            currentViewState.copy(isOpenAddProperty = true,
                    errorSource = null)
        }
        else {
            Log.e("tag", "emit result")
            currentViewState.copy(isOpenAddProperty = false,
                    errorSource = ErrorSource.NO_AGENT_IN_DB)
            Log.e("current view", currentViewState.toString())
            viewStateLD.value = currentViewState
        }

    }
}