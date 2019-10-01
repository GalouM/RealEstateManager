package com.openclassrooms.realestatemanager.addProperty

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openclassrooms.realestatemanager.addProperty.ActionType.MODIFY_PROPERTY
import com.openclassrooms.realestatemanager.addProperty.ActionType.NEW_PROPERTY
import com.openclassrooms.realestatemanager.addProperty.ErrorSourceAddProperty.*
import com.openclassrooms.realestatemanager.data.TempProperty
import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApiResponse
import com.openclassrooms.realestatemanager.data.database.Converters
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.repository.SaveDataRepository
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import com.openclassrooms.realestatemanager.utils.*
import com.openclassrooms.realestatemanager.utils.extensions.*
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
        private val currencyRepository: CurrencyRepository,
        private val saveDataRepository: SaveDataRepository)
    : BaseViewModel<AddPropertyViewState>(), REMViewModel<AddPropertyIntent, AddPropertyResult>,
BitmapDownloader.Listeners{

    private var currentViewState = AddPropertyViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private val viewEffectLD = MutableLiveData<AddPropertyViewEffect>()
    val viewEffect: LiveData<AddPropertyViewEffect>
        get() = viewEffectLD

    val currency: LiveData<Currency>
        get() = currencyRepository.currency

    private lateinit var disposable: Disposable
    private var initalIntentHandled = false

    private lateinit var actionType: ActionType
    private lateinit var context: Context
    private var propertyId: String = idGenerated
    private var previousPictures = listOf<Picture>()
    private var previousAmenities = listOf<Amenity>()
    private lateinit var address: Address
    private lateinit var property: Property
    private val amenities = mutableListOf<Amenity>()
    private var pictures = mutableListOf<Picture>()

    // data
    private val listErrorInputs = mutableListOf<ErrorSourceAddProperty>()
    private lateinit var neighborhood: String
    private var agentId: String? = null


    //Coroutine job
    private var addPropertyJob: Job? = null
    private var addPropertyDataJob: Job? = null
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
                        intent.amenities, intent.context
                )

            }

            is AddPropertyIntent.OpenListAgentsIntent -> fetchAgentsFromDB()
            is AddPropertyIntent.InitialIntent -> setActionType(intent.actionType)
            is AddPropertyIntent.SelectAgentIntent -> setAgentSelected(intent.agentId)
            is AddPropertyIntent.AddPictureToListIntent -> addPictureToProperty(intent.pictureUrl, intent.thumbnailUrl)
            is AddPropertyIntent.RemovePictureFromListIntent -> removePictureFromList(intent.picture)
            is AddPropertyIntent.MovePictureInListPositionIntent -> movePictureInList(intent.from, intent.to)
            is AddPropertyIntent.AddDescriptionToPicture -> addDescriptionToPicture(intent.position, intent.description)
            is AddPropertyIntent.SaveDraftIntent -> savePropertyForLater(
                    intent.type, intent.price,
                    intent.surface, intent.rooms,
                    intent.bedrooms, intent.bathrooms,
                    intent.description, intent.address,
                    intent.neighborhood, intent.onMarketSince,
                    intent.isSold, intent.sellDate,
                    intent.amenities
            )
            is AddPropertyIntent.DisplayDataFromDB -> fetchExistingPropertyFromDB()
        }
    }

    override fun resultToViewState(result: Lce<AddPropertyResult>) {
        currentViewState = when (result){
            is Lce.Content ->{
                when(result.packet){
                    is AddPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                isADraft = false,
                                listAgents = result.packet.listAgents,
                                isLoading = false)
                    }

                    is AddPropertyResult.PropertyAddedToDBResult -> {
                        currentViewState.copy(
                                isADraft = false,
                                isSavedToDB = true,
                                listAgents = null,
                                errors = null,
                                isLoading = false
                        )
                    }
                    is AddPropertyResult.PictureResult -> {
                        currentViewState.copy(
                                isADraft = false,
                                isSavedToDB = false,
                                listAgents = null,
                                errors = null,
                                isLoading = false,
                                pictures = result.packet.pictures
                        )
                    }
                    AddPropertyResult.PropertyAddedToDraftResult -> {
                        currentViewState.copy(
                                isADraft = false,
                                isSavedToDraft = true,
                                listAgents = null,
                                errors = null,
                                isLoading = false
                        )
                    }
                    else -> {
                        currentViewState.copy(
                                isADraft = false,
                                listAgents = null,
                                errors = null,
                                isLoading = false
                        )
                    }
                }
            }

            is Lce.Loading ->{
                currentViewState.copy(
                        isADraft = false,
                        listAgents = null,
                        errors = null,
                        isLoading = true)

            }

            is Lce.Error ->{
                when(result.packet){
                    is AddPropertyResult.PropertyAddedToDBResult -> {
                        currentViewState.copy(
                                isADraft = false,
                                listAgents = null,
                                errors = result.packet.errorSource,
                                isLoading = false)
                    }

                    is AddPropertyResult.ListAgentsResult -> {
                        currentViewState.copy(
                                isADraft = false,
                                listAgents = null,
                                isLoading = false,
                                errors = result.packet.errorSource
                        )
                    }
                    is AddPropertyResult.PropertyFromDBResult -> {
                        currentViewState.copy(
                                isADraft = false,
                                listAgents = null,
                                isLoading = false,
                                errors = result.packet.errorSource
                        )
                    }
                    else -> {
                        currentViewState.copy(
                                isADraft = false,
                                listAgents = null,
                                isLoading = false,
                                errors = null
                        )
                    }
                }

            }
        }
    }

    private fun resultToViewEffect(result: Lce<AddPropertyResult>){
        if(result is Lce.Content){
            currentViewState.copy(
                    isADraft = false,
                    listAgents = null,
                    errors = null,
                    isLoading = false
            )
            when(result.packet){
                is AddPropertyResult.PropertyFromDBResult -> {
                    viewEffectLD.value = AddPropertyViewEffect.PropertyFromDBEffect(
                            result.packet.type, result.packet.price, result.packet.surface, result.packet.bedrooms,
                            result.packet.rooms, result.packet.bathrooms, result.packet.description, result.packet.address,
                            result.packet.neighborhood, result.packet.onMarketSince, result.packet.isSold ?: false,
                            result.packet.sellOn ?: "", result.packet.agent?.id, result.packet.amenities,
                            result.packet.agent?.firstName ?: "",result.packet.agent?.lastName ?: ""
                    )
                }
                is AddPropertyResult.PropertyFromDraftResult -> viewEffectLD.value = AddPropertyViewEffect.PropertyfromDraftEffect(
                        result.packet.type, result.packet.price, result.packet.surface, result.packet.bedrooms,
                        result.packet.rooms, result.packet.bathrooms, result.packet.description, result.packet.address,
                        result.packet.neighborhood, result.packet.onMarketSince, result.packet.isSold ?: false,
                        result.packet.sellOn ?: "", result.packet.agent?.id, result.packet.amenities,
                        result.packet.agent?.firstName ?: "",result.packet.agent?.lastName ?: "",
                        result.packet.originalAvailable
                )
            }
        }
    }

    //--------------------
    // INITIAL INTENT
    //--------------------

    private fun setActionType(actionType: ActionType){
        if(!initalIntentHandled) {
            this.actionType = actionType
            setDataProperty()
            fetchSavedProperty()
            initalIntentHandled = true
        }
    }

    private fun setDataProperty(){
        if(actionType == MODIFY_PROPERTY){
            val propertyFromRepository = propertyRepository.propertyPicked!!
            property = propertyFromRepository.property
            address = propertyFromRepository.address[0]
            propertyId = property.id
            agentId = property.agent
            previousAmenities = propertyFromRepository.amenities
            previousPictures = propertyFromRepository.pictures

        }
    }


    //--------------------
    // SET DATA
    //--------------------

    private fun setAgentSelected(agentId: String){
        this.agentId = agentId
    }

    private fun addPictureToProperty(pictureUrl: String, thumbnailUrl: String?){
        pictures.add(Picture(
                idGenerated, pictureUrl, thumbnailUrl,  null, propertyId, "", pictures.size -1)
        )
        emitResultPictureModification()
    }

    private fun removePictureFromList(picture: Picture){
        pictures.remove(picture)
        createOrderNumberPictures()
        emitResultPictureModification()
    }

    private fun movePictureInList(from: Int, to: Int){
        val fromPhoto = pictures[from]
        pictures.removeAt(from)
        pictures.add(to, fromPhoto)
        createOrderNumberPictures()
    }

    private fun addDescriptionToPicture(position: Int, description: String){
        pictures[position].description = description
    }

    private fun createOrderNumberPictures(){
        pictures.forEachIndexed { index, picture ->
            picture.orderNumber = index
        }
    }

    private fun emitResultPictureModification(){
        val result: Lce<AddPropertyResult> = Lce.Content(AddPropertyResult.PictureResult(pictures))
        resultToViewState(result)
    }

    //--------------------
    // ADD PROPERTY TO DB
    //--------------------

    private fun receivePropertyData(type: String, price: Double?,
                                    surface: Double?, rooms: Int?,
                                    bedrooms: Int?, bathrooms: Int?,
                                    description: String, address: String,
                                    neighborhood: String, onMarketSince: String,
                                    isSold: Boolean, sellOn: String?,
                                    amenities: List<TypeAmenity>, contextApp: Context
    ){

        fun checkErrorsFromUserInput(): List<ErrorSourceAddProperty>{
            val listErrors = mutableListOf<ErrorSourceAddProperty>()
            val onMarketDate = onMarketSince.toDate()
            val sellDate = sellOn?.toDate()

            if(onMarketDate == null ||
                    !onMarketDate.isCorrectOnMarketDate()) listErrors.add(INCORRECT_ON_MARKET_DATE)
            if(isSold) {
                if (sellDate == null ||
                        onMarketDate != null
                        && !sellDate.isCorrectSoldDate(onMarketDate)) listErrors.add(INCORRECT_SOLD_DATE)
                if (sellOn == null) listErrors.add(NO_SOLD_DATE)
            }
            if(!type.isExistingPropertyType()) listErrors.add(NO_TYPE_SELECTED)
            if(price == null) listErrors.add(NO_PRICE)
            if(surface == null) listErrors.add(NO_SURFACE)
            if(rooms == null) listErrors.add(NO_ROOMS)
            if(address.isEmpty()) listErrors.add(NO_ADDRESS)
            if(agentId == null) listErrors.add(NO_AGENT)
            pictures.forEach {
                if(it.description.isBlank()){
                    listErrors.add(MISSING_DESCRIPTION)
                }
            }

            return listErrors
        }
        fun setDataFromInputs() {
            context = contextApp

            property = Property(
                    id = propertyId, type = Converters.toTypeProperty(type), price = price!!.toEuro(currency.value!!),
                    surface = surface!!.toSqMeter(currency.value!!), rooms = rooms!!, bedrooms = bedrooms, bathrooms = bathrooms,
                    description = description, onMarketSince = onMarketSince.toDate()!!, sold = isSold, sellDate =  sellOn?.toDate(),
                    agent = agentId!!, hasPictures = pictures.isNotEmpty(), creationDate = todaysDate
            )
            this.address = Address(propertyId = property.id)
            amenities.forEach {
                this.amenities.add(Amenity(idGenerated, propertyId, it))
            }
            this.neighborhood = neighborhood
        }

        listErrorInputs.clear()
        resultToViewState(Lce.Loading())
        checkErrorsFromUserInput().forEach { listErrorInputs.add(it) }
        if(listErrorInputs.isEmpty()){
            setDataFromInputs()
            fetchAddressLocation(address)
        } else {
            emitErrorFromInputs()
        }
    }

    private fun checkLocationAndMap(geocodingApi: GeocodingApiResponse){
        val results = geocodingApi.results
        if(results.isNotEmpty()){
            configureAddressComponent(geocodingApi)
            val mapUrl = propertyRepository.getMapLocation(
                    address.latitude.toString(),address.longitude.toString()
            ).toUrl()
            if(mapUrl != null) {
                fetchBitmapMap(mapUrl)

            } else {
                listErrorInputs.add(ERROR_FETCHING_MAP)
                emitErrorFromInputs()
            }


        } else {
            listErrorInputs.add(TOO_MANY_ADDRESS)
            emitErrorFromInputs()
        }
    }

    override fun onBitmapDownloaded(bitmap: Bitmap) {
        address.mapIconUrl = bitmap.saveToInternalStorage(context, property.id + generateName(), TypeImage.ICON_MAP).toString()
        propertyRepository.uploadMapInNetwork(bitmap, property.id)
                .addOnSuccessListener { emitResultAddPropertyToView() }
                .addOnFailureListener {
                    listErrorInputs.add(ERROR_FETCHING_MAP)
                    emitErrorFromInputs()
                }

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
                listErrorInputs.add(INCORECT_ADDRESS)
                emitErrorFromInputs()
            }

            override fun onComplete() {}
        }
    }

    private fun configureAddressComponent(geocodingApi: GeocodingApiResponse){
        val results = geocodingApi.results
        val result = results[0]
        address.latitude = result.geometry.location.lat
        address.longitude = result.geometry.location.lng
        address.addressForDisplay = result.formattedAddress
        var streetNumber = ""
        var streetName = ""
        result.addressComponents.forEach { component ->
            component.types.forEach {
                when(it){
                    "street_number" -> streetNumber = component.longName
                    "route" -> streetName = component.longName
                    "locality" -> address.city = component.longName
                    "administrative_area_level_1" -> address.state = component.shortName
                    "country" -> address.country = component.longName
                    "postal_code" -> address.postalCode = component.longName
                    "neighborhood" -> neighborhood = if(neighborhood.isEmpty()) component.longName else neighborhood
                }
            }
        }
        address.neighbourhood = if(neighborhood.isEmpty()) address.city else neighborhood
        address.street = "$streetNumber $streetName"
    }

    private fun fetchBitmapMap(mapUrl: URL){
        BitmapDownloader(this).execute(mapUrl)
    }

    private fun emitResultAddPropertyToView(){
        if(addPropertyJob?.isActive == true) addPropertyJob?.cancel()
        if(addPropertyDataJob?.isActive == true) addPropertyDataJob?.cancel()

        val newPictures = mutableListOf<Picture>()
        val picturesToUpdate = mutableListOf<Picture>()
        val pictureToDelete = mutableListOf<Picture>()
        val errorOnPropertyCreation = mutableListOf<ErrorSourceAddProperty>()

        fun emitResult(){
            val result: Lce<AddPropertyResult> = if(errorOnPropertyCreation.isEmpty()) {
                when (actionType) {
                    NEW_PROPERTY -> saveDataRepository.tempProperty = null
                    MODIFY_PROPERTY -> saveDataRepository.saveModifiedProperty(null, property.id)
                }
                Lce.Content(AddPropertyResult.PropertyAddedToDBResult(null))
            } else {
                Lce.Error(AddPropertyResult.PropertyAddedToDBResult(errorOnPropertyCreation))
            }

            resultToViewState(result)
        }

        fun createPropertyAndDataInDB(){
            addPropertyDataJob = launch {
                propertyRepository.createPropertyAndData(
                        property, newPictures, pictureToDelete, picturesToUpdate, amenities, address, actionType, previousAmenities
                )
                        .addOnSuccessListener { emitResult() }
                        .addOnFailureListener{
                            errorOnPropertyCreation.add(UPLOAD_DATA)
                            emitResult()
                        }
            }

        }

        fun orderPictures(){
            if(actionType == MODIFY_PROPERTY && previousPictures.isNotEmpty()){
                pictures.forEach { picture ->
                    val previousPicture = previousPictures.find{ it.id == picture.id}
                    if(previousPicture != null){
                        picturesToUpdate.add(picture)

                    } else {
                        newPictures.add(picture)
                    }
                }
                previousPictures.forEach {picture ->
                    if(!pictures.any { it.id == picture.id }) pictureToDelete.add(picture)
                }
            } else {
                newPictures.addAll(pictures)
            }
        }

        orderPictures()
        createPropertyAndDataInDB()

    }

    private fun emitErrorFromInputs(){
        val result: Lce<AddPropertyResult> = Lce.Error(AddPropertyResult.PropertyAddedToDBResult(listErrorInputs))
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
                val listErrors = listOf(ERROR_FETCHING_AGENTS)
                Lce.Error(AddPropertyResult.ListAgentsResult(null, listErrors))
            } else{
                Lce.Content(AddPropertyResult.ListAgentsResult(agents, null))
            }
            resultToViewState(result)
        }

    }


    //--------------------
    // SAVE FOR LATER
    //--------------------
    private fun savePropertyForLater(
            type: String, price: Double?,
            surface: Double?, rooms: Int?,
            bedrooms: Int?, bathrooms: Int?,
            description: String, address: String,
            neighborhood: String, onMarketSince: String,
            isSold: Boolean, sellOn: String?,
            amenities: List<TypeAmenity>
    ){
        resultToViewState(Lce.Loading())

        val tempProperty = TempProperty(
                id = propertyId, type = type, price = price, surface = surface, rooms = rooms,
                bedrooms = bedrooms, bathrooms = bathrooms, description = description, onMarketSince = onMarketSince,
                isSold = isSold, sellDate = sellOn, agent = agentId, address = address, neighborhood = neighborhood,
                pictures = pictures, amenities = amenities
        )

        when(actionType){
            NEW_PROPERTY -> saveDataRepository.tempProperty = tempProperty
            MODIFY_PROPERTY -> saveDataRepository.saveModifiedProperty(tempProperty, propertyId)
        }

        val result: Lce<AddPropertyResult> = Lce.Content(AddPropertyResult.PropertyAddedToDraftResult)
        resultToViewState(result)

    }

    //--------------------
    // FETCH SAVED PROPERTY
    //--------------------
    private fun fetchSavedProperty(){
        resultToViewState(Lce.Loading())

        val savedProperty = when(actionType){
            NEW_PROPERTY -> saveDataRepository.tempProperty
            MODIFY_PROPERTY -> saveDataRepository.getSavedModifyProperty(propertyId)
        }
        var agent: Agent? = null

        fun emitResult(){
            val isOriginalAvailable = actionType == MODIFY_PROPERTY
            val result: Lce<AddPropertyResult>  = Lce.Content(AddPropertyResult.PropertyFromDraftResult(
                    savedProperty!!.type, savedProperty.price, savedProperty.surface, savedProperty.rooms,
                    savedProperty.bedrooms, savedProperty. bathrooms, savedProperty.description, savedProperty.address,
                    savedProperty.neighborhood, savedProperty.onMarketSince, savedProperty.isSold,
                    savedProperty.sellDate, savedProperty.amenities,
                    agent, isOriginalAvailable
            ))
        resultToViewEffect(result)
        }

        if(savedProperty == null){
            when(actionType){
                MODIFY_PROPERTY -> fetchExistingPropertyFromDB()
                NEW_PROPERTY -> {
                    val result: Lce<AddPropertyResult> = Lce.Content(AddPropertyResult.NewPropertyResult)
                    resultToViewState(result)
                }
            }
        } else {
            propertyId = savedProperty.id
            agentId = savedProperty.agent
            agentId?.let {
                searchAgentsJob = launch {
                    agent = agentRepository.getAgent(it)[0]
                }
            }
            emitResult()
            pictures = savedProperty.pictures.sortedBy { picture -> picture.orderNumber }.toMutableList()
            emitResultPictureModification()

        }

    }

    //--------------------
    // FETCH EXISING PROPERTY
    //--------------------

    private fun fetchExistingPropertyFromDB(){
        resultToViewState(Lce.Loading())
        if(searchAgentsJob?.isActive == true) searchAgentsJob?.cancel()

        var agent: Agent? = null

        fun emitResult(){
            val result: Lce<AddPropertyResult>
            result = Lce.Content(AddPropertyResult.PropertyFromDBResult(
                    property.type.typeName, property.price, property.surface, property.rooms,
                    property.bedrooms, property. bathrooms, property.description, address.addressForDisplay,
                    address.neighbourhood, property.onMarketSince.toStringForDisplay(), property.sold,
                    property.sellDate?.toStringForDisplay(), previousAmenities.map { it.type },
                    agent,  null
            ))

            resultToViewEffect(result)
        }

        fun fetchAgent(){
            searchAgentsJob = launch {
                agent = agentRepository.getAgent(agentId!!)[0]
                emitResult()
            }
        }

        fetchAgent()
        fetchPictureExistingPictureFromDB()

    }

    private fun fetchPictureExistingPictureFromDB(){
        resultToViewState(Lce.Loading())
        pictures = previousPictures.sortedBy { it.orderNumber }.toMutableList()
        emitResultPictureModification()

    }

}