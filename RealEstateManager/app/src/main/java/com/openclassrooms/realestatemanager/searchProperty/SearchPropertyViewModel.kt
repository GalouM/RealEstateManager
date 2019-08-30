package com.openclassrooms.realestatemanager.searchProperty

import android.util.Log
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-08-28
 */

class SearchPropertyViewModel(
        private val agentRepository: AgentRepository,
        private val propertyRepository: PropertyRepository,
        private val currencyRepository: CurrencyRepository
) : BaseViewModel<SeachPropertyViewState>(), REMViewModel<SearchPropertyIntent, SearchPropertyResult>{

    private var currentViewState = SeachPropertyViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private var searchAgentsJob: Job? = null
    private var searchPropertyJob: Job? = null

    override fun actionFromIntent(intent: SearchPropertyIntent) {
        when(intent){
            is SearchPropertyIntent.SearchPropertyFromInputIntent -> {
                searchProperties(
                        intent.type, intent.minPrice, intent.maxPrice, intent.minSurface, intent.maxSurface,
                        intent.minNbRooms, intent.minNbBedrooms, intent.minNbBathrooms, intent.neighborhood,
                        intent.stillOnMarket, intent.manageBy, intent.closeTo, intent.maxDateOnMarket
                )
            }
            is SearchPropertyIntent.ChangeCurrencyIntent -> changeCurrency()
            is SearchPropertyIntent.GetListAgentsIntent -> fetchAgentsFromDB()
            is SearchPropertyIntent.GetCurrentCurrencyIntent -> emitCurrentCurrency()
        }
    }

    override fun resultToViewState(result: Lce<SearchPropertyResult>) {
        currentViewState = when(result){
            is Lce.Content -> {
                when(result.packet){
                    is SearchPropertyResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                agents = null,
                                currency = result.packet.currency,
                                loading = false
                        )
                    }
                    is SearchPropertyResult.SearchResult -> {
                        currentViewState.copy(
                                agents = null,
                                error = null,
                                loading = false,
                                showProperty = true
                        )
                    }
                    is SearchPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                error = null,
                                loading = false,
                                agents = result.packet.listAgents
                        )
                    }
                }
            }
            is Lce.Loading -> {
                currentViewState.copy(
                        agents = null,
                        loading = true
                )
            }
            is Lce.Error -> {
                when(result.packet){
                    is SearchPropertyResult.SearchResult -> {
                        currentViewState.copy(
                                agents = null,
                                error = result.packet.error,
                                loading = false
                        )
                    }

                    is SearchPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                agents = null,
                                error = result.packet.errorSource,
                                loading = false
                        )
                    }
                    else -> {
                        currentViewState.copy(
                                agents = null,
                                loading = false
                        )
                    }
                }
            }
        }
    }

    private fun searchProperties(
            type: List<TypeProperty>, minPrice: Double?, maxPrice: Double?,
            minSurface: Double?, maxSurface: Double?, minNbRooms: Int?,
            minNbBedrooms: Int?, minNbBathrooms: Int?, neighborhood: String?,
            stillOnMarket: Boolean?, manageBy: List<Int>?, closeTo: List<TypeAmenity>,
            maxDateOnMarket: String?
    ){
        resultToViewState(Lce.Loading())

        val listErrors = mutableListOf<ErrorSourceSearch>()
        if (type.isEmpty()) listErrors.add(ErrorSourceSearch.NO_TYPE_SELECTED)
        if (manageBy == null || manageBy.isEmpty()) listErrors.add(ErrorSourceSearch.NO_AGENT_SELECTED)

        if(listErrors.isNotEmpty()){
            val result: Lce<SearchPropertyResult> = Lce.Error(SearchPropertyResult.SearchResult(listErrors))
            resultToViewState(result)
        } else{
            if(searchPropertyJob?.isActive == true) searchPropertyJob?.cancel()

            val minPriceQuery = minPrice ?: 0.0
            val maxPriceQuery = maxPrice ?: 99999999999999.0
            val minSurfaceQuery = minSurface ?: 0.0
            val maxSurfaceQuery = maxSurface ?: 99999999999999999.0
            val nbRoomQuery = minNbRooms ?: 0
            val nbBedroomQuery = minNbBedrooms ?: 0
            val nbBathroomQuery = minNbBathrooms ?: 0
            val neighborhoodQuery = if(neighborhood!!.isEmpty()) "%" else neighborhood
            val isSoldQuery = if(stillOnMarket!!) listOf(0) else listOf(0, 1)

            var propertiesQuery: List<Property> = listOf()

            if(closeTo.isEmpty()){
                searchAgentsJob = launch {
                    propertiesQuery = propertyRepository.getPropertiesQuery(
                            minPriceQuery, maxPriceQuery, minSurfaceQuery, maxSurfaceQuery,
                            nbRoomQuery, nbBedroomQuery, nbBathroomQuery, manageBy!!, type,
                            neighborhoodQuery, isSoldQuery
                    )
                }
            } else {
                searchAgentsJob = launch {
                    val propertyFromDB = propertyRepository.getPropertiesQuery(
                            minPriceQuery, maxPriceQuery, minSurfaceQuery, maxSurfaceQuery,
                            nbRoomQuery, nbBedroomQuery, nbBathroomQuery, manageBy!!, type, neighborhoodQuery,
                            isSoldQuery, closeTo
                    )

                    propertiesQuery = keepOnlyPropertyIfHasAllAmenities(closeTo.size, propertyFromDB)
                }
            }

            if(propertiesQuery.isEmpty()){
                val listErrors = listOf(ErrorSourceSearch.NO_PROPERTY_FOUND)
                val result: Lce<SearchPropertyResult> = Lce.Error(SearchPropertyResult.SearchResult(listErrors))
                resultToViewState(result)
            } else{
                val result: Lce<SearchPropertyResult> = Lce.Content(SearchPropertyResult.SearchResult(null))
                resultToViewState(result)
            }

        }

    }

    private fun keepOnlyPropertyIfHasAllAmenities(nbAmenities: Int, properties: List<Property>): List<Property>{
        if(nbAmenities == 1) return  properties
        val idPropertyToKeep = properties
                .groupingBy { it.id }
                .eachCount()
                .filterValues { it == nbAmenities }.keys

        val propertyToDisplay = mutableListOf<Property>()
        idPropertyToKeep.forEach { id ->
            properties.find { it.id == id }?.let{
                propertyToDisplay.add(it)
            }
        }

        return propertyToDisplay

    }

    //--------------------
    // CURRENCY
    //--------------------

    private fun changeCurrency(){
        currencyRepository.setCurrency()
        emitCurrentCurrency()
    }

    private fun emitCurrentCurrency(){
        val currency = currencyRepository.currency.value!!
        val result: Lce<SearchPropertyResult> = Lce.Content(SearchPropertyResult.ChangeCurrencyResult(currency))

        resultToViewState(result)
    }

    //--------------------
    // FETCH AGENTS
    //--------------------

    private fun fetchAgentsFromDB(){
        resultToViewState(Lce.Loading())

        if(searchAgentsJob?.isActive == true) searchAgentsJob?.cancel()

        searchAgentsJob = launch {
            val agents: List<Agent>? = agentRepository.getAllAgents()
            val result: Lce<SearchPropertyResult> = if(agents == null || agents.isEmpty()){
                val listErrors = listOf(ErrorSourceSearch.ERROR_FETCHING_AGENTS)
                Lce.Error(SearchPropertyResult.ListAgentsResult(null, listErrors))
            } else{
                Lce.Content(SearchPropertyResult.ListAgentsResult(agents, null))
            }
            resultToViewState(result)
        }

    }
}