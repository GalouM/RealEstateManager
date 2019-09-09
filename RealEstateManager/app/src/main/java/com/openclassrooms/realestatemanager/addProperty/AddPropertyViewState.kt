package com.openclassrooms.realestatemanager.addProperty

import android.content.Context
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState
import com.openclassrooms.realestatemanager.utils.TypeAmenity

/**
 * Created by galou on 2019-07-27
 */

data class AddPropertyViewState(
        val isModifyProperty: Boolean = false,
        val isLoading: Boolean = false,
        val isSaved: Boolean = false,
        val errors: List<ErrorSourceAddProperty>? = null,
        val listAgents: List<Agent>? = null,
        val type: String = "",
        val price: Double? = null, val surface: Double? = null,
        val bedrooms: Int? = null, val rooms: Int? = null,
        val bathrooms: Int? = null, val description: String? = null,
        val address: String = "", val neighborhood: String = "",
        val onMarketSince: String = "", val isSold: Boolean = false,
        val sellDate: String? = null, val agentId: Int? = null,
        val amenities: List<TypeAmenity>? = null, val pictures: List<Picture>? = null,
        val agentFirstName: String = "", val agentLastName: String = ""

) : REMViewState

sealed class AddPropertyIntent : REMIntent{
    data class AddPropertyToDBIntent(
            val type: String, val price: Double?,
            val surface: Double?, val rooms: Int?,
            val bedrooms: Int?, val bathrooms: Int?,
            val description: String, val address: String,
            val neighborhood: String, val onMarketSince: String,
            val isSold: Boolean, val sellDate: String?,
            val amenities: List<TypeAmenity>, val context: Context
    ) : AddPropertyIntent()

    data class SelectAgentIntent(val agentId: Int): AddPropertyIntent()

    data class InitialIntent(val actionType: ActionType) : AddPropertyIntent()

    object OpenListAgentsIntent : AddPropertyIntent()

    data class AddPictureToList(val pictureUrl: String, val thumbnailUrl: String?) : AddPropertyIntent()

    data class RemovePictureFromList(val picture: Picture) : AddPropertyIntent()

    data class MovePictureInListPosition(val from: Int, val to: Int) : AddPropertyIntent()

    data class AddDescriptionToPicture(val position: Int, val description: String) : AddPropertyIntent()

}

sealed class AddPropertyResult : REMResult{
    data class AddPropertyToDBResult(val errorSource: List<ErrorSourceAddProperty>?) : AddPropertyResult()
    data class FetchedPropertyResult(
            val property: Property?, val amenities: List<TypeAmenity>?,
            val agent: Agent?, val address: Address?, val errorSource: List<ErrorSourceAddProperty>?
    ) : AddPropertyResult()
    data class ListAgentsResult(val listAgents: List<Agent>?, val errorSource: List<ErrorSourceAddProperty>?) : AddPropertyResult()
    data class PictureResult(val pictures: List<Picture>) : AddPropertyResult()
}