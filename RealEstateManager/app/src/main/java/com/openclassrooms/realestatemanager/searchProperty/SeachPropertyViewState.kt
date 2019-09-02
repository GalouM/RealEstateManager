package com.openclassrooms.realestatemanager.searchProperty

import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.mviBase.REMIntent
import com.openclassrooms.realestatemanager.mviBase.REMResult
import com.openclassrooms.realestatemanager.mviBase.REMViewState
import com.openclassrooms.realestatemanager.utils.Currency
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty

/**
 * Created by galou on 2019-08-29
 */

data class SeachPropertyViewState(
        val error: List<ErrorSourceSearch>? = null,
        val showProperty: Boolean = false,
        val agents: List<Agent>? = null,
        val loading: Boolean = false
) : REMViewState

sealed class SearchPropertyIntent : REMIntent{
    data class SearchPropertyFromInputIntent(
            val type: List<TypeProperty>, val minPrice: Double?, val maxPrice: Double?,
            val minSurface: Double?, val maxSurface: Double?, val minNbRooms: Int?,
            val minNbBedrooms: Int?, val minNbBathrooms: Int?, val neighborhood: String?,
            val stillOnMarket: Boolean?, val manageBy: List<Int>?, val closeTo: List<TypeAmenity>,
            val maxDateOnMarket: String?, val hasPhotos: Boolean?
    ) : SearchPropertyIntent()

    object GetListAgentsIntent : SearchPropertyIntent()
}

sealed class SearchPropertyResult : REMResult{
    data class SearchResult(val error: List<ErrorSourceSearch>?) : SearchPropertyResult()
    data class ListAgentsResult(val listAgents: List<Agent>?, val errorSource: List<ErrorSourceSearch>?) : SearchPropertyResult()
}