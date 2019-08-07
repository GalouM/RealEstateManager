package com.openclassrooms.realestatemanager.addProperty

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.PropertyRepository
import com.openclassrooms.realestatemanager.data.database.Converters
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.extensions.*
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Created by galou on 2019-07-27
 */
class AddPropertyViewModel (
        private val agentRepository: AgentRepository,
        private val propertyRepository: PropertyRepository)
    : BaseViewModel(){

    private val viewStateLD = MutableLiveData<AddPropertyViewState>()
    val viewState: LiveData<AddPropertyViewState>
        get() = viewStateLD
    private var currentViewState = AddPropertyViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private var addPropertyJob: Job? = null
    private var addAddressJob: Job? = null
    private var addAmenitiesJob: Job? = null
    private var addPicturesJob: Job? = null
    private var searchAgentsJob: Job? = null

    private var currentCurency = Currency.EURO

    fun actionFromIntent(intent: AddPropertyIntent) {
        when(intent) {
            is AddPropertyIntent.AddPropertyToDBIntent -> {
                addPropertyToDB(
                        intent.type, intent.price,
                        intent.surface, intent.rooms,
                        intent.bedrooms, intent.bathrooms,
                        intent.description, intent.address,
                        intent.neighborhood, intent.onMarketSince,
                        intent.isSold, intent.sellDate,
                        intent.agent, intent.amenities,
                        intent.pictures, intent.pictureDescription)

            }

            is AddPropertyIntent.ChangeCurrencyIntent -> changeCurrency()

            is AddPropertyIntent.OpenListAgentsIntent -> fetchAgentsFromDB()

        }
    }

    private fun resultToViewState(result: Lce<AddPropertyResult>) {
        currentViewState = when (result){
            is Lce.Content ->{
                when(result.packet){
                    is AddPropertyResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                currency = result.packet.currency)
                    }

                    is AddPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                openListAgents = true,
                                listAgents = result.packet.listAgents,
                                isLoading = false)
                    }

                    is AddPropertyResult.AddPropertyToDBResult -> {
                        currentViewState.copy(
                                isLoading = false,
                                isSaved = true
                        )
                    }
                }
            }

            is Lce.Loading ->{
                currentViewState.copy(isLoading = true)

            }

            is Lce.Error ->{
                when(result.packet){
                    is AddPropertyResult.AddPropertyToDBResult -> {
                        currentViewState.copy(
                                errors = result.packet.errorSource,
                                isLoading = false)
                    }

                    is AddPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                isLoading = false,
                                errors = result.packet.errorSource
                        )
                    }
                    is AddPropertyResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                isLoading = false
                        )
                    }
                }

            }
        }
    }

    private fun addPropertyToDB(type: String, price: String,
                                surface: String, rooms: String,
                                bedrooms: String, bathrooms: String,
                                description: String, address: String,
                                neighborhood: String, onMarketSince: String?,
                                isSold: Boolean, sellOn: String?,
                                agent: Int?, amenities: List<TypeAmenity>?,
                                pictures: List<String>?, pictureDescription: String?){

        resultToViewState(Lce.Loading())
        if(addPropertyJob?.isActive == true) addPropertyJob?.cancel()
        if(addAddressJob?.isActive == true) addAddressJob?.cancel()
        if(addAmenitiesJob?.isActive == true) addAmenitiesJob?.cancel()
        if(addPicturesJob?.isActive == true) addPicturesJob?.cancel()

        val listErrorInputs = mutableListOf<ErrorSourceAddProperty>()
        val onMarketDate = onMarketSince?.toDate()
        val sellDate = sellOn?.toDate()

        fun checkErrors(){
            if(onMarketDate == null ||
                    !onMarketDate.isCorrectOnMarketDate()) listErrorInputs.add(ErrorSourceAddProperty.INCORRECT_ON_MARKET_DATE)
            if(sellDate == null ||
                    onMarketDate != null
                    && !sellDate.isCorrectSoldDate(onMarketDate)) listErrorInputs.add(ErrorSourceAddProperty.INCORRECT_SOLD_DATE)
            if(isSold && sellOn == null) listErrorInputs.add(ErrorSourceAddProperty.NO_SOLD_DATE)
            if(!type.isExistingPropertyType()) listErrorInputs.add(ErrorSourceAddProperty.NO_TYPE_SELECTED)
            if(price.isEmpty() || price.toDoubleOrNull() == null) listErrorInputs.add(ErrorSourceAddProperty.NO_PRICE)
            if(surface.isEmpty() || surface.toDoubleOrNull() == null) listErrorInputs.add(ErrorSourceAddProperty.NO_SURFACE)
            if(rooms.isEmpty() || rooms.toIntOrNull() == null) listErrorInputs.add(ErrorSourceAddProperty.NO_ROOMS)
            if(address.isEmpty()) listErrorInputs.add(ErrorSourceAddProperty.NO_ADDRESS)
            if(neighborhood.isEmpty()) listErrorInputs.add(ErrorSourceAddProperty.NO_NEIGHBORHOOD)
            if(onMarketSince == null) listErrorInputs.add(ErrorSourceAddProperty.NO_ON_MARKET_DATE)
            if(agent == null) listErrorInputs.add(ErrorSourceAddProperty.NO_AGENT)
        }

        fun createAmenitiesInDB(propertyId: Int){
            addAmenitiesJob = launch {
                for(amenity in amenities!!){
                    val amenityForDB = Amenity(null, propertyId, amenity)
                    propertyRepository.insertAmenity(amenityForDB)
                }
            }
        }

        fun createPicturesInDB(propertyId: Int){
            addPicturesJob = launch {
                for(picture in pictures!!){
                    val pictureForDB = Picture(picture, propertyId, pictureDescription)
                    propertyRepository.insertPicture(pictureForDB)
                }
            }

        }

        fun createAddressInDB(propertyId: Int){
            addAddressJob = launch {
                val addressForDB = Address(propertyId, address, 123.345, 123.345, neighborhood)
                propertyRepository.createAddress(addressForDB)

            }

        }

        fun createNewPropertyInDB(){
            addPropertyJob = launch {
                val propertyForDB = Property(
                        null, Converters.toTypeProperty(type), price.toDouble().toEuro(currentCurency),
                        surface.toDouble().toSqMeter(currentCurency), rooms.toInt(),
                        bedrooms.toIntOrNull(), bathrooms.toIntOrNull(),
                        description, onMarketSince!!,
                        isSold, sellOn, agent!!)
                val propertyId = propertyRepository.createProperty(propertyForDB).toInt()

                if(pictures != null && pictures.isNotEmpty()){
                    createPicturesInDB(propertyId)
                }
                if(amenities != null && amenities.isNotEmpty()){
                    createAmenitiesInDB(propertyId)
                }

                createAddressInDB(propertyId)

                val result: Lce<AddPropertyResult> = Lce.Content(AddPropertyResult.AddPropertyToDBResult(null))
                resultToViewState(result)
            }

        }

        checkErrors()

        if(listErrorInputs.isEmpty()){
            createNewPropertyInDB()



        } else {
            val result: Lce<AddPropertyResult> = Lce.Error(AddPropertyResult.AddPropertyToDBResult(listErrorInputs))
            resultToViewState(result)
        }

    }

    private fun changeCurrency(){
        val result: Lce<AddPropertyResult>
        result = when(currentCurency){
            Currency.EURO -> {
                currentCurency = Currency.DOLLAR
                Lce.Content(AddPropertyResult.ChangeCurrencyResult(Currency.DOLLAR))
            }
            Currency.DOLLAR -> {
                currentCurency = Currency.EURO
                Lce.Content(AddPropertyResult.ChangeCurrencyResult(Currency.EURO))
            }
        }

        resultToViewState(result)

    }

    private fun fetchAgentsFromDB(){
        resultToViewState(Lce.Loading())

        if(searchAgentsJob?.isActive == true) searchAgentsJob?.cancel()

        searchAgentsJob = launch {
            val agents: List<Agent>? = agentRepository.getAllAgents()
            val result: Lce<AddPropertyResult> = if(agents == null || agents.isEmpty()){
                val listErrors = listOf(ErrorSourceAddProperty.ERROR_FETCHING_AGENTS)
                Lce.Error(AddPropertyResult.ListAgentsResult(null, listErrors))
            } else{
                Lce.Content(AddPropertyResult.ListAgentsResult(agents, null))
            }
            resultToViewState(result)
        }

    }

}