package com.openclassrooms.realestatemanager.listProperties

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.entity.PropertyWithAllData
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.displayData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-08-12
 */
class ListPropertyViewModel(
        private val propertyRepository: PropertyRepository,
        private val currencyRepository: CurrencyRepository
)
    : BaseViewModel<PropertyListViewState>(), REMViewModel<PropertyListIntent, PropertyListResult>{

    private var currentViewState = PropertyListViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    val currency: LiveData<Currency>
        get() = currencyRepository.currency

    private var actionType = ActionTypeList.ALL_PROPERTIES

    private var searchPropertiesJob: Job? = null

    override fun actionFromIntent(intent: PropertyListIntent){
        when(intent){
            is PropertyListIntent.DisplayPropertiesIntent -> fetchPropertiesFromDB()
            is PropertyListIntent.OpenPropertyDetailIntent -> setPropertySelected(intent.property)
            is PropertyListIntent.SetActionTypeIntent -> setActionType(intent.actionType)
        }

    }

    override fun resultToViewState(result: Lce<PropertyListResult>){
        currentViewState = when (result){
            is Lce.Content -> {
                when(result.packet){
                    is PropertyListResult.DisplayPropertiesResult -> {
                        currentViewState.copy(
                                openDetails = false,
                                errorSource = null,
                                isLoading = false,
                                listProperties = result.packet.properties
                        )
                    }
                    is PropertyListResult.OpenPropertyDetailResult -> {
                        currentViewState.copy(
                                errorSource = null,
                                isLoading = false,
                                openDetails = true
                        )
                    }
                }
            }

            is Lce.Loading -> {
                currentViewState.copy(
                        errorSource = null,
                        openDetails = false,
                        isLoading = true
                )
            }
            is Lce.Error -> {
                when(result.packet){
                    is PropertyListResult.DisplayPropertiesResult -> {
                        currentViewState.copy(
                                openDetails = false,
                                isLoading = false,
                                errorSource = ErrorSourceListProperty.NO_PROPERTY_IN_DB
                        )
                    }
                    else -> throw Exception("unknow error")
                }
            }
        }
    }

    private fun setActionType(actionType: ActionTypeList){
        this.actionType = actionType
    }

    private fun setPropertySelected(property: PropertyWithAllData){
        propertyRepository.propertyPicked = property
        propertyRepository.propertyPicked?.let {
            val result: Lce<PropertyListResult> = Lce.Content(PropertyListResult.OpenPropertyDetailResult)
            resultToViewState(result)
        }

    }

    private fun fetchPropertiesFromDB() {
        resultToViewState(Lce.Loading())
        if(searchPropertiesJob?.isActive == true) searchPropertiesJob?.cancel()

        var propertiesForDisplay: List<PropertyWithAllData>? = null

        fun emitResult(){
            displayData("properties dipslay $propertiesForDisplay")
            val result: Lce<PropertyListResult> = if(propertiesForDisplay!!.isEmpty()){
                Lce.Error(PropertyListResult.DisplayPropertiesResult(null))
            } else{
                Lce.Content(PropertyListResult.DisplayPropertiesResult(propertiesForDisplay))
            }
            resultToViewState(result)
        }

        displayData("action type $actionType")

        when(actionType){
            ActionTypeList.ALL_PROPERTIES -> {
                searchPropertiesJob = launch {
                    propertiesForDisplay = propertyRepository.getAllProperties()
                    emitResult()
                }

            }
            ActionTypeList.SEARCH_RESULT -> {
                propertiesForDisplay = propertyRepository.propertyFromSearch
                emitResult()
            }
        }


    }

}