package com.openclassrooms.realestatemanager.listProperties

import androidx.lifecycle.LiveData
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.PropertyForListDisplay
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
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

    private var searchPropertiesJob: Job? = null

    override fun actionFromIntent(intent: PropertyListIntent){
        when(intent){
            is PropertyListIntent.DisplayPropertiesIntent -> fetchPropertiesFromDB()
            is PropertyListIntent.OpenPropertyDetailIntent -> setPropertySelected(intent.idProperty)
        }

    }

    override fun resultToViewState(result: Lce<PropertyListResult>){
        currentViewState = when (result){
            is Lce.Content -> {
                when(result.packet){
                    is PropertyListResult.DisplayPropertiesResult -> {
                        currentViewState.copy(
                                openDetails = false,
                                isLoading = false,
                                listProperties = result.packet.properties
                        )
                    }
                    is PropertyListResult.OpenPropertyDetailResult -> {
                        currentViewState.copy(
                                isLoading = false,
                                openDetails = true
                        )
                    }
                }
            }

            is Lce.Loading -> {
                currentViewState.copy(
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

    private fun setPropertySelected(id: Int){
        propertyRepository.setIdPropertyPicked(id)
        val result: Lce<PropertyListResult> = Lce.Content(PropertyListResult.OpenPropertyDetailResult)
        resultToViewState(result)
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