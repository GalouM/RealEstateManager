package com.openclassrooms.realestatemanager.addProperty

import android.content.Context
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.TypeAmenity

/**
 * Created by galou on 2019-07-27
 */

data class AddPropertyViewState(
        val isLoading: Boolean = false,
        val isSaved: Boolean = false,
        val errors: List<ErrorSourceAddProperty>? = null,
        val openListAgents: Boolean = false,
        val listAgents: List<Agent>? = null,
        val currency: Currency = Currency.EURO
) : REMViewState

sealed class AddPropertyIntent : REMIntent{
    data class AddPropertyToDBIntent(
            val type: String, val price: String,
            val surface: String, val rooms: String,
            val bedrooms: String, val bathrooms: String,
            val description: String, val address: String,
            val neighborhood: String, val onMarketSince: String,
            val isSold: Boolean, val sellDate: String?,
            val agent: Int?, val amenities: List<TypeAmenity>,
            val pictures: List<String>?, val pictureDescription: String?,
            val context: Context
    ) : AddPropertyIntent()

    object ChangeCurrencyIntent : AddPropertyIntent()

    object OpenListAgentsIntent : AddPropertyIntent()

    object GetCurrentCurrencyIntent : AddPropertyIntent()

}

sealed class AddPropertyResult : REMResult{
    data class AddPropertyToDBResult(val errorSource: List<ErrorSourceAddProperty>?) : AddPropertyResult()
    data class ListAgentsResult(val listAgents: List<Agent>?, val errorSource: List<ErrorSourceAddProperty>?) : AddPropertyResult()
    data class ChangeCurrencyResult(val currency: Currency) : AddPropertyResult()
}