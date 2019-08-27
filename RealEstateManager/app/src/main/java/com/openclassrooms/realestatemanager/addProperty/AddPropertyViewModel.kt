package com.openclassrooms.realestatemanager.addProperty

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.CurrencyRepository
import com.openclassrooms.realestatemanager.data.PropertyRepository
import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApiResponse
import com.openclassrooms.realestatemanager.data.database.Converters
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.extensions.*
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import com.openclassrooms.realestatemanager.utils.BitmapDownloader
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL

/**
 * Created by galou on 2019-07-27
 */
class AddPropertyViewModel (
        private val agentRepository: AgentRepository,
        private val propertyRepository: PropertyRepository,
        private val currencyRepository: CurrencyRepository)
    : BaseViewModel<AddPropertyViewState>(), REMViewModel<AddPropertyIntent, AddPropertyResult>,
BitmapDownloader.Listeners{

    private var currentViewState = AddPropertyViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private lateinit var disposable: Disposable

    // data
    private val listErrorInputs = mutableListOf<ErrorSourceAddProperty>()
    private lateinit var type: String
    private lateinit var price: String
    private lateinit var surface: String
    private lateinit var rooms: String
    private lateinit var bedrooms: String
    private lateinit var bathrooms: String
    private lateinit var description: String
    private lateinit var address: String
    private lateinit var street: String
    private lateinit var city: String
    private lateinit var postalCode: String
    private lateinit var state: String
    private lateinit var country: String
    private lateinit var neighborhood: String
    private lateinit var onMarketSince: String
    private var isSold: Boolean = false
    private var sellOn: String? = null
    private var agent: Int? = null
    private lateinit var amenities: List<TypeAmenity>
    private var pictures: List<String>? = null
    private var pictureDescription: String? = null
    private var map = ""
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var context: Context? = null
    private var propertyId: Int? = null
    private var idFromApi = ""


    //Coroutine job
    private var addPropertyJob: Job? = null
    private var addAddressJob: Job? = null
    private var addAmenitiesJob: Job? = null
    private var addPicturesJob: Job? = null
    private var searchAgentsJob: Job? = null


    override fun actionFromIntent(intent: AddPropertyIntent) {
        when(intent) {
            is AddPropertyIntent.AddPropertyToDBIntent -> {
                receivePropertyData(
                        intent.type, intent.price,
                        intent.surface, intent.rooms,
                        intent.bedrooms, intent.bathrooms,
                        intent.description, intent.address,
                        intent.neighborhood, intent.onMarketSince,
                        intent.isSold, intent.sellDate,
                        intent.agent, intent.amenities,
                        intent.pictures, intent.pictureDescription, intent.context)

            }

            is AddPropertyIntent.ChangeCurrencyIntent -> changeCurrency()

            is AddPropertyIntent.OpenListAgentsIntent -> fetchAgentsFromDB()

            is AddPropertyIntent.GetCurrentCurrencyIntent -> emitCurrentCurrency()
        }
    }

    override fun resultToViewState(result: Lce<AddPropertyResult>) {
        currentViewState = when (result){
            is Lce.Content ->{
                when(result.packet){
                    is AddPropertyResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                currency = result.packet.currency,
                                openListAgents = false)
                    }

                    is AddPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                openListAgents = true,
                                listAgents = result.packet.listAgents,
                                isLoading = false)
                    }

                    is AddPropertyResult.AddPropertyToDBResult -> {
                        currentViewState.copy(
                                errors = null,
                                isLoading = false,
                                isSaved = true,
                                openListAgents = false
                        )
                    }
                }
            }

            is Lce.Loading ->{
                currentViewState.copy(
                        isLoading = true,
                        openListAgents = false)

            }

            is Lce.Error ->{
                when(result.packet){
                    is AddPropertyResult.AddPropertyToDBResult -> {
                        currentViewState.copy(
                                errors = result.packet.errorSource,
                                isLoading = false,
                                openListAgents = false)
                    }

                    is AddPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                isLoading = false,
                                errors = result.packet.errorSource,
                                openListAgents = false
                        )
                    }
                    is AddPropertyResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                isLoading = false,
                                openListAgents = false
                        )
                    }
                }

            }
        }
    }

    //--------------------
    // ADD PROPERTY TO DB
    //--------------------

    private fun receivePropertyData(type: String, price: String,
                                    surface: String, rooms: String,
                                    bedrooms: String, bathrooms: String,
                                    description: String, address: String,
                                    neighborhood: String, onMarketSince: String,
                                    isSold: Boolean, sellOn: String?,
                                    agent: Int?, amenities: List<TypeAmenity>,
                                    pictures: List<String>?, pictureDescription: String?,
                                    contextApp: Context){
        fun setGlobalProperties() {
            context = contextApp
            this.type = type
            this.price = price
            this.surface = surface
            this.rooms = rooms
            this.bedrooms = bedrooms
            this.bathrooms = bathrooms
            this.description = description
            this.address = address
            this.neighborhood = neighborhood
            this.onMarketSince = onMarketSince
            this.isSold = isSold
            this.sellOn = sellOn
            this.agent = agent
            this.amenities = amenities
            this.pictures = pictures
            this.pictureDescription = pictureDescription
        }

        listErrorInputs.clear()
        resultToViewState(Lce.Loading())
        setGlobalProperties()
        checkErrorsFromUserInput().forEach { listErrorInputs.add(it) }
        fetchAddressLocation(address)
    }

    private fun checkLocationAndMap(geocodingApi: GeocodingApiResponse){
        if(isLocationCorrect(geocodingApi)){
            val mapUrl = fetchMapFromApi(latitude!!, longitude!!)
            if( mapUrl != null){
                fetchBitmapMap(mapUrl)
            } else {
                emitResultAddPropertyToView()
            }
        } else {
            listErrorInputs.add(ErrorSourceAddProperty.TOO_MANY_ADDRESS)
            emitResultAddPropertyToView()
        }
    }

    override fun onBitmapDownloaded(bitmap: Bitmap) {
        map = bitmap.saveToInternalStorage(context, idFromApi).toString()
        emitResultAddPropertyToView()
    }

    private fun checkErrorsFromUserInput(): List<ErrorSourceAddProperty>{

        val listErrors = mutableListOf<ErrorSourceAddProperty>()
        val onMarketDate = onMarketSince.toDate()
        val sellDate = sellOn?.toDate()

        if(onMarketDate == null ||
                !onMarketDate.isCorrectOnMarketDate()) listErrors.add(ErrorSourceAddProperty.INCORRECT_ON_MARKET_DATE)
        if(isSold) {
            if (sellDate == null ||
                    onMarketDate != null
                    && !sellDate.isCorrectSoldDate(onMarketDate)) listErrors.add(ErrorSourceAddProperty.INCORRECT_SOLD_DATE)
            if (sellOn == null) listErrors.add(ErrorSourceAddProperty.NO_SOLD_DATE)
        }
        if(!type.isExistingPropertyType()) listErrors.add(ErrorSourceAddProperty.NO_TYPE_SELECTED)
        if(price.isEmpty() || price.toDoubleOrNull() == null) listErrors.add(ErrorSourceAddProperty.NO_PRICE)
        if(surface.isEmpty() || surface.toDoubleOrNull() == null) listErrors.add(ErrorSourceAddProperty.NO_SURFACE)
        if(rooms.isEmpty() || rooms.toIntOrNull() == null) listErrors.add(ErrorSourceAddProperty.NO_ROOMS)
        if(address.isEmpty()) listErrors.add(ErrorSourceAddProperty.NO_ADDRESS)
        if(agent == null) listErrors.add(ErrorSourceAddProperty.NO_AGENT)

        return listErrors
    }

    private fun fetchAddressLocation(address: String){
        disposable = propertyRepository.getLocationFromAddress(address.convertForApi())
                .subscribeWith(getObserverGeocodingApi())
    }

    private fun getObserverGeocodingApi(): DisposableObserver<GeocodingApiResponse>{
        return object : DisposableObserver<GeocodingApiResponse>() {
            override fun onNext(geocodingApi: GeocodingApiResponse) {
                checkLocationAndMap(geocodingApi)
            }

            override fun onError(e: Throwable) {
                listErrorInputs.add(ErrorSourceAddProperty.INCORECT_ADDRESS)
                emitResultAddPropertyToView()
            }

            override fun onComplete() {}
        }
    }


    private fun isLocationCorrect(geocodingApi: GeocodingApiResponse): Boolean{
        val results = geocodingApi.results
        if(results.isEmpty()) return false
        val result = results[0]
        latitude = result.geometry.location.lat
        longitude = result.geometry.location.lng
        var streetNumber = ""
        var streetName = ""
        result.addressComponents.forEach { component ->
            component.types.forEach {
                when(it){
                    "street_number" -> streetNumber = component.longName
                    "route" -> streetName = component.longName
                    "locality" -> city = component.longName
                    "administrative_area_level_1" -> state = component.shortName
                    "country" -> country = component.longName
                    "postal_code" -> postalCode = component.longName
                    "neighborhood" -> neighborhood.isEmpty().let { neighborhood = component.longName }
                }
            }
        }
        neighborhood = if(neighborhood.isEmpty()) city else neighborhood
        idFromApi = result.placeId
        street = "$streetNumber $streetName"
        return true



    }

    private fun fetchMapFromApi(lat: Double, lng: Double): URL? {
        return propertyRepository.getMapLocation(lat.toString(), lng.toString()).toUrl()
    }

    private fun fetchBitmapMap(mapUrl: URL){
        BitmapDownloader(this).execute(mapUrl)
    }

    private fun emitResultAddPropertyToView(){
        if(addPropertyJob?.isActive == true) addPropertyJob?.cancel()
        if(addAddressJob?.isActive == true) addAddressJob?.cancel()
        if(addAmenitiesJob?.isActive == true) addAmenitiesJob?.cancel()
        if(addPicturesJob?.isActive == true) addPicturesJob?.cancel()

        fun createAmenitiesInDB(){
            addAmenitiesJob = launch {
                for(amenity in amenities){
                    val amenityForDB = Amenity(null, propertyId!!, amenity)
                    propertyRepository.insertAmenity(amenityForDB)
                }
            }
        }

        fun createPicturesInDB(){
            addPicturesJob = launch {
                for(picture in pictures!!){
                    val pictureForDB = Picture(picture, propertyId!!, pictureDescription)
                    propertyRepository.insertPicture(pictureForDB)
                }
            }

        }

        fun createAddressInDB(){
            addAddressJob = launch {
                val addressForDB = Address(
                        propertyId!!, street, city, postalCode, country, state,
                        longitude!!, latitude!!, neighborhood, map
                )
                propertyRepository.createAddress(addressForDB)
            }

        }

        fun createNewPropertyInDB(){
            addPropertyJob = launch {
                val currency = currencyRepository.currency.value!!
                val propertyForDB = Property(
                        null, Converters.toTypeProperty(type), price.toDouble().toEuro(currency),
                        surface.toDouble().toSqMeter(currency), rooms.toInt(),
                        bedrooms.toIntOrNull(), bathrooms.toIntOrNull(),
                        description, onMarketSince,
                        isSold, sellOn, agent!!)
                propertyId = propertyRepository.createProperty(propertyForDB).toInt()
                Log.e("prop", propertyForDB.toString())

                if(pictures != null && pictures!!.isNotEmpty()){
                    createPicturesInDB()
                }
                if(amenities.isNotEmpty()){
                    createAmenitiesInDB()
                }

                createAddressInDB()

                val result: Lce<AddPropertyResult> = Lce.Content(AddPropertyResult.AddPropertyToDBResult(null))
                resultToViewState(result)
            }

        }

        if(listErrorInputs.isEmpty()){
            createNewPropertyInDB()
        } else {
            val result: Lce<AddPropertyResult> = Lce.Error(AddPropertyResult.AddPropertyToDBResult(listErrorInputs))
            resultToViewState(result)
        }

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
        val result: Lce<AddPropertyResult> = Lce.Content(AddPropertyResult.ChangeCurrencyResult(currency))

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