package com.openclassrooms.realestatemanager.detailsProperty

import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState
import com.openclassrooms.realestatemanager.utils.Currency

/**
 * Created by galou on 2019-08-25
 */

data class DetailsPropertyViewState(
        val isLoading: Boolean = false,
        val property: Property? = null,
        val address: Address? = null,
        val pictures: List<Picture>? = null,
        val amenities: List<Amenity>? = null,
        val currency: Currency = Currency.EURO,
        val modifyProperty: Boolean = false
) : REMViewState

sealed class DetailsPropertyIntent : REMIntent{
    object FetchDetailsIntent : DetailsPropertyIntent()

    object ModifyPropertyIntent : DetailsPropertyIntent()

    object ChangeCurrencyIntent : DetailsPropertyIntent()

    object GetCurrentCurrencyIntent : DetailsPropertyIntent()
}

sealed class DetailsPropertyResult : REMResult {
    data class FetchDetailsResult(
            val property: Property?, val address: Address?,
            val amenities: List<Amenity>?, val pictures: List<Picture>?
    ) : DetailsPropertyResult()
    object ModifyPropertyResult : DetailsPropertyResult()
    data class ChangeCurrencyResult(val currency: Currency) : DetailsPropertyResult()
}