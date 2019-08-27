package com.openclassrooms.realestatemanager.addProperty

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.openclassrooms.realestatemanager.data.AgentRepository
import com.openclassrooms.realestatemanager.data.CurrencyRepository
import com.openclassrooms.realestatemanager.data.PropertyRepository
import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApi
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
    private var map: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var context: Context? = null
    private var propertyId: Int? = null


    //Coroutine job
    private var addPropertyJob: Job? = null
    private var addAddressJob: Job? = null
    private var addAmenitiesJob: Job? = null
    private var addPicturesJob: Job? = null
    private var searchAgentsJob: Job? = null


    override fun actionFromIntent(intent: AddPropertyIntent) {
        when(intent) {
            is AddPropertyIntent.AddPropertyToDBIntent -> {
                checkErrorsFromUserInput(
                        intent.type, intent.price,
                        intent.surface, intent.rooms,
                        intent.bedrooms, intent.bathrooms,
                        intent.description, intent.address,
                        intent.neighborhood, intent.onMarketSince,
                        intent.isSold, intent.sellDate,
                        intent.agent, intent.amenities,
                        intent.pictures, intent.pictureDescription)
                configureContext(intent.context)

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

    private fun configureContext(contextApp: Context){
        context = contextApp
    }

    private fun checkErrorsFromUserInput(type: String, price: String,
                                         surface: String, rooms: String,
                                         bedrooms: String, bathrooms: String,
                                         description: String, address: String,
                                         neighborhood: String, onMarketSince: String,
                                         isSold: Boolean, sellOn: String?,
                                         agent: Int?, amenities: List<TypeAmenity>,
                                         pictures: List<String>?, pictureDescription: String?){
        resultToViewState(Lce.Loading())

        listErrorInputs.clear()
        val onMarketDate = onMarketSince.toDate()
        val sellDate = sellOn?.toDate()

        if(onMarketDate == null ||
                !onMarketDate.isCorrectOnMarketDate()) listErrorInputs.add(ErrorSourceAddProperty.INCORRECT_ON_MARKET_DATE)
        if(isSold) {
            if (sellDate == null ||
                    onMarketDate != null
                    && !sellDate.isCorrectSoldDate(onMarketDate)) listErrorInputs.add(ErrorSourceAddProperty.INCORRECT_SOLD_DATE)
            if (sellOn == null) listErrorInputs.add(ErrorSourceAddProperty.NO_SOLD_DATE)
        }
        if(!type.isExistingPropertyType()) listErrorInputs.add(ErrorSourceAddProperty.NO_TYPE_SELECTED)
        if(price.isEmpty() || price.toDoubleOrNull() == null) listErrorInputs.add(ErrorSourceAddProperty.NO_PRICE)
        if(surface.isEmpty() || surface.toDoubleOrNull() == null) listErrorInputs.add(ErrorSourceAddProperty.NO_SURFACE)
        if(rooms.isEmpty() || rooms.toIntOrNull() == null) listErrorInputs.add(ErrorSourceAddProperty.NO_ROOMS)
        if(address.isEmpty()) listErrorInputs.add(ErrorSourceAddProperty.NO_ADDRESS)
        if(agent == null) listErrorInputs.add(ErrorSourceAddProperty.NO_AGENT)

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

        fetchAddressLocationAndMap()
    }

    private fun fetchAddressLocationAndMap(){
        disposable = propertyRepository.getLocationAndMapFromAddress(address.convertForApi())
                .subscribeWith(getObserverGeocodingApi())
    }

    private fun getObserverGeocodingApi(): DisposableObserver<GeocodingApi>{
        return object : DisposableObserver<GeocodingApi>() {
            override fun onNext(geocodingApi: GeocodingApi) {
                checkLocationAnMapAreCorrect(geocodingApi)
            }

            override fun onError(e: Throwable) {
                listErrorInputs.add(ErrorSourceAddProperty.INCORECT_ADDRESS)
                emitResultAddPropertyToView()
            }

            override fun onComplete() {}
        }
    }


    private fun checkLocationAnMapAreCorrect(geocodingApi: GeocodingApi){
        val results = geocodingApi.results
        if(results.size == 1 && results[0].locations.size == 1){
            val location = results[0].locations[0]
            latitude = location.latLng.lat
            longitude = location.latLng.lng
            neighborhood = if(neighborhood.isEmpty()){
                if(location.neighborhood.isEmpty()) {
                    location.city
                } else location.neighborhood
            } else neighborhood
            street = location.street
            city = location.city
            postalCode = location.postalCode
            country = location.country
            state = location.state
            address = results[0].providedLocation.location
            fetchBitmapMap(location.mapUrl.toUrl()!!)
        } else {
            listErrorInputs.add(ErrorSourceAddProperty.TOO_MANY_ADDRESS)
            emitResultAddPropertyToView()

        }

    }

    private fun fetchBitmapMap(mapUrl: URL){
        BitmapDownloader(this).execute(mapUrl)
    }

    override fun onBitmapDownloaded(bitmap: Bitmap) {
        map = bitmap.saveToInternalStorage(context, propertyId.toString()).toString()
        emitResultAddPropertyToView()
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
                        longitude!!, latitude!!, neighborhood, map!!
                )
                propertyRepository.createAddress(addressForDB)
                Log.e("[address", propertyId.toString())
                Log.e("[addressid", addressForDB.propertyId.toString())

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
                Log.e("[roperty", propertyId.toString())

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