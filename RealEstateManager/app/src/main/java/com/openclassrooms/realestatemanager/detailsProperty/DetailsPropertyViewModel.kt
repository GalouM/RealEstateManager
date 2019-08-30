package com.openclassrooms.realestatemanager.detailsProperty

import android.util.Log
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-08-25
 */

class DetailsPropertyViewModel(
        private val propertyRepository: PropertyRepository,
        private val currencyRepository: CurrencyRepository
) : BaseViewModel<DetailsPropertyViewState>(),
        REMViewModel<DetailsPropertyIntent, DetailsPropertyResult>{

    private var currentViewState = DetailsPropertyViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private var searchPropertyJob: Job? = null
    private var searchAddressJob: Job? = null
    private var searchAmenitiesJob: Job? = null
    private var searchPicturesJob: Job? = null

    override fun actionFromIntent(intent: DetailsPropertyIntent) {
        when(intent){
            is DetailsPropertyIntent.FetchDetailsIntent -> fetchDetailsProperty()
            is DetailsPropertyIntent.ModifyPropertyIntent -> modifyProperty()
            is DetailsPropertyIntent.ChangeCurrencyIntent -> changeCurrency()
            is DetailsPropertyIntent.GetCurrentCurrencyIntent -> emitCurrentCurrency()
        }
    }

    override fun resultToViewState(result: Lce<DetailsPropertyResult>) {
        currentViewState = when (result){
            is Lce.Content -> {
                when(result.packet){
                    is DetailsPropertyResult.FetchDetailsResult -> {
                        currentViewState.copy(
                                modifyProperty = false,
                                isLoading = false,
                                property = result.packet.property,
                                address = result.packet.address,
                                amenities = result.packet.amenities,
                                pictures = result.packet.pictures
                        )
                    }

                    is DetailsPropertyResult.ModifyPropertyResult -> {
                        currentViewState.copy(
                                modifyProperty = true,
                                isLoading = false
                        )
                    }

                    is DetailsPropertyResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                modifyProperty = false,
                                isLoading = false,
                                currency = result.packet.currency
                        )
                    }
                }
            }
            is Lce.Loading -> {
                currentViewState.copy(
                        modifyProperty = false,
                        isLoading = true
                )
            }
            is Lce.Error -> {
                currentViewState.copy(
                        modifyProperty = false,
                        isLoading = false
                )

            }

        }
    }

    private fun fetchDetailsProperty(){
        resultToViewState(Lce.Loading())
        if(searchPropertyJob?.isActive == true) searchPropertyJob?.cancel()
        if(searchAddressJob?.isActive == true) searchAddressJob?.cancel()
        if(searchAmenitiesJob?.isActive == true) searchAmenitiesJob?.cancel()
        if(searchPicturesJob?.isActive == true) searchPicturesJob?.cancel()

        val propertyId = propertyRepository.getPropertyPickedId()!!

        var property: Property? = null
        var address: Address? = null
        var amenities: List<Amenity>? = null
        var pictures: List<Picture>? = null

        fun emitResult(){
            val result: Lce<DetailsPropertyResult> = Lce.Content(DetailsPropertyResult.FetchDetailsResult(
                    property, address, amenities, pictures
            ))
            resultToViewState(result)
        }

        fun fetchPictures(){
            searchPicturesJob = launch {
                pictures = propertyRepository.getPropertyPicture(propertyId)
                emitResult()
            }
        }

        fun fetchAmenities(){
            searchAmenitiesJob = launch {
                amenities = propertyRepository.getPropertyAmenities(propertyId)
                fetchPictures()
            }
        }

        fun fetchAddress(){
            searchAddressJob = launch {
                address = propertyRepository.getPropertyAddress(propertyId)[0]
                fetchAmenities()
            }
        }

        fun fetchProperty(){
            searchPropertyJob = launch {
                property = propertyRepository.getProperty(propertyId)[0]
                fetchAddress()
            }
        }

        fetchProperty()

    }

    private fun modifyProperty(){
        resultToViewState(Lce.Loading())
        val result: Lce<DetailsPropertyResult> = Lce.Content(DetailsPropertyResult.ModifyPropertyResult)
        resultToViewState(result)
    }

    //--------------------
    // CURRENCY
    //--------------------

    private fun changeCurrency(){
        resultToViewState(Lce.Loading())
        currencyRepository.setCurrency()
        emitCurrentCurrency()
    }

    private fun emitCurrentCurrency(){
        val currency = currencyRepository.currency.value!!
        val result: Lce<DetailsPropertyResult> = Lce.Content(DetailsPropertyResult.ChangeCurrencyResult(currency))

        resultToViewState(result)
    }
}