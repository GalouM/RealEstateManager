package com.openclassrooms.realestatemanager.listProperties

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.data.PropertyRepository
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import com.openclassrooms.realestatemanager.utils.Currency
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-08-12
 */
class ListPropertyViewModel(
        private val propertyRepository: PropertyRepository)
    : BaseViewModel<PropertyListViewState>(), REMViewModel<PropertyListIntent, PropertyListResult>{

    private var currentViewState = PropertyListViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private val currencyLD = MutableLiveData<Currency>()
    val currency: LiveData<Currency> = currencyLD

    private var searchPropertiesJob: Job? = null

    override fun actionFromIntent(intent: PropertyListIntent){
        when(intent){
            is PropertyListIntent.DisplayPropertiesIntent -> fetchPropertiesFromDB()
        }

    }

    override fun resultToViewState(result: Lce<PropertyListResult>){
        currentViewState = when (result){
            is Lce.Content -> {
                when(result.packet){
                    is PropertyListResult.DisplayPropertiesResult -> {
                        currentViewState.copy(
                                isLoading = false,
                                listProperties = result.packet.properties
                        )
                    }
                }
            }

            is Lce.Loading -> {
                currentViewState.copy(
                        isLoading = true
                )
            }
            is Lce.Error -> {
                when(result.packet){
                    is PropertyListResult.DisplayPropertiesResult -> {
                        currentViewState.copy(
                                isLoading = false,
                                errorSource = ErrorSourceListProperty.NO_PROPERTY_IN_DB
                        )
                    }
                }
            }
        }
    }

    private fun fetchPropertiesFromDB() {
        resultToViewState(Lce.Loading())
        if(searchPropertiesJob?.isActive == true) searchPropertiesJob?.cancel()

        var neighborhood = ""
        var pictureUrl = ""
        var latitude: Double = 0.0
        var longitude: Double = 0.0
        val propertiesForDisplay = mutableListOf<PropertyForListDisplay>()

        fun emitResult(){
            val result: Lce<PropertyListResult> = if(propertiesForDisplay.isEmpty()){
                Lce.Error(PropertyListResult.DisplayPropertiesResult(null))
            } else{
                Lce.Content(PropertyListResult.DisplayPropertiesResult(propertiesForDisplay))
            }
            resultToViewState(result)
        }

        fun configurePropertyForDisplay(property: Property){
            val propertyToDisplay = PropertyForListDisplay(
                    property.id!!, property.type.typeName, neighborhood,
                    latitude, longitude, property.price, pictureUrl, property.sold)
            propertiesForDisplay.add(propertyToDisplay)

            emitResult()
        }

        fun fetchAddress(idProperty: Int, property: Property){
            launch {
                val propertyAddress = propertyRepository.getPropertyAddress(idProperty)[0]
                neighborhood = propertyAddress.neighbourhood
                longitude = propertyAddress.longitude
                latitude = propertyAddress.latitude
                configurePropertyForDisplay(property)
            }
        }

        fun fetchPicture(idProperty: Int, property: Property){
            launch {
                val pictures = propertyRepository.getPropertyPicture(idProperty)
                if(pictures.isNotEmpty()) {
                    pictureUrl = pictures[0].url
                }

                fetchAddress(idProperty, property)
            }
        }

        searchPropertiesJob = launch {
            val properties: List<Property>? = propertyRepository.getAllProperties()
            properties?.forEach {
                fetchPicture(it.id!!, it)
            }

        }
    }

}