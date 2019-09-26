package com.openclassrooms.realestatemanager.mainActivity

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.data.repository.AgentRepository
import com.openclassrooms.realestatemanager.data.repository.CurrencyRepository
import com.openclassrooms.realestatemanager.data.repository.PropertyRepository
import com.openclassrooms.realestatemanager.data.repository.SaveDataRepository
import com.openclassrooms.realestatemanager.mainActivity.ErrorSourceMainActivity.*
import com.openclassrooms.realestatemanager.mviBase.BaseViewModel
import com.openclassrooms.realestatemanager.mviBase.Lce
import com.openclassrooms.realestatemanager.mviBase.REMViewModel
import com.openclassrooms.realestatemanager.utils.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

/**
 * Created by galou on 2019-07-04
 */
class MainActivityViewModel(
        private val agentRepository: AgentRepository,
        private val currencyRepository: CurrencyRepository,
        private val propertyRepository: PropertyRepository,
        private val saveDataRepository: SaveDataRepository
) : BaseViewModel<MainActivityViewState>(), REMViewModel<MainActivityIntent, MainActivityResult>{

    private var currentViewState = MainActivityViewState()
        set(value) {
            field = value
            viewStateLD.value = value
        }

    private val newAgents = mutableListOf<Agent>()
    private val newProperty = mutableListOf<Property>()
    private val newAmenities = mutableListOf<Amenity>()
    private val newPictures = mutableListOf<Picture>()
    private val newAddresses = mutableListOf<Address>()
    private var latestUpdate: Date?
        get() = saveDataRepository.lastUpdateFromNetwork
        set(value){
            saveDataRepository.lastUpdateFromNetwork = value
        }



    private var searchAgentsJob: Job? = null
    private var createPropertiesAndDataJob: Job? = null

     override fun actionFromIntent(intent: MainActivityIntent){
        when(intent){
            is MainActivityIntent.OpenAddPropertyActivityIntent -> onOpenAddPropertyRequest()
            is MainActivityIntent.ChangeCurrencyIntent -> changeCurrency()
            is MainActivityIntent.GetCurrentCurrencyIntent -> emitCurrentCurrency()
            is MainActivityIntent.UpdatePropertyFromNetwork -> downloadLatestDataFromNetwork(intent.context)
        }

    }

     override fun resultToViewState(result: Lce<MainActivityResult>){
        currentViewState = when (result){
            is Lce.Content -> {
                when(result.packet){
                    is MainActivityResult.OpenAddPropertyResult ->{
                        currentViewState.copy(
                                propertyAdded = null,
                                isOpenAddProperty = true,
                                errorSource = null,
                                isLoading = false)
                    }
                    is MainActivityResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                propertyAdded = null,
                                isOpenAddProperty = false,
                                currency = result.packet.currency
                                )
                    }
                    is MainActivityResult.UpdateDataFromNetwork -> {
                        currentViewState.copy(
                                isOpenAddProperty = false,
                                errorSource = null,
                                isLoading = false,
                                propertyAdded = result.packet.numberPropertyAdded
                        )
                    }
                }
            }

            is Lce.Loading -> {
                currentViewState.copy(
                        propertyAdded = null,
                        isLoading = true,
                        errorSource = null,
                        isOpenAddProperty = false)
            }
            is Lce.Error -> {
                when(result.packet){
                    is MainActivityResult.OpenAddPropertyResult -> {
                        currentViewState.copy(
                                propertyAdded = null,
                                isOpenAddProperty = false,
                                errorSource = NO_AGENT_IN_DB,
                                isLoading = false
                        )
                    }
                    is MainActivityResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                propertyAdded = null,
                                isOpenAddProperty = false
                        )
                    }
                    is MainActivityResult.UpdateDataFromNetwork -> {
                        currentViewState.copy(
                                propertyAdded = null,
                                isOpenAddProperty = false,
                                errorSource = result.packet.errorSource,
                                isLoading = false
                        )
                    }
                }
            }
        }
    }

    private fun onOpenAddPropertyRequest(){
        resultToViewState(Lce.Loading())
        if(searchAgentsJob?.isActive == true) searchAgentsJob?.cancel()

        searchAgentsJob = launch {
            val agents: List<Agent>? = agentRepository.getAllAgents()
            val result: Lce<MainActivityResult> = if(agents == null || agents.isEmpty()){
                Lce.Error(MainActivityResult.OpenAddPropertyResult)
            } else{
                Lce.Content(MainActivityResult.OpenAddPropertyResult)
            }
            resultToViewState(result)
        }

    }

    private fun changeCurrency(){
        currencyRepository.setCurrency()
        emitCurrentCurrency()

    }

    private fun emitCurrentCurrency(){
        val currency = currencyRepository.currency.value!!
        val result: Lce<MainActivityResult> = Lce.Content(MainActivityResult.ChangeCurrencyResult(currency))

        resultToViewState(result)
    }

    private fun downloadLatestDataFromNetwork(context: Context){
        resultToViewState(Lce.Loading())

        propertyRepository.getAllPropertiesFromNetwork(latestUpdate)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        task.result?.documents?.forEach { document ->
                            document.toObject(Property::class.java)?.let { newProperty.add(it) }
                        }
                        getDataAndAgent(context)
                    } else emitResultNetworkRequestFailure(ERROR_FETCHING_NEW_FROM_NETWORK)
                }


    }

    private fun getDataAndAgent(context: Context){
        val networkOperations = mutableListOf<Task<*>>()

        var dataEmpty = false

        runBlocking {
            val agents = agentRepository.getAllAgents()
            val properties = propertyRepository.getAllProperties()
            dataEmpty = agents.isEmpty() && properties.isEmpty()
        }

        latestUpdate = if(dataEmpty) null else latestUpdate

        val agentsDownload = agentRepository.getAgentsFromNetwork(latestUpdate)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.documents?.forEach { document ->
                            document.toObject(Agent::class.java)?.let {newAgents.add(it) }
                        }
                    }
                }
        networkOperations.add(agentsDownload)

        newProperty.forEach {property ->
            val propertyId = property.id
            val amenitiesDownload = propertyRepository.getAmenitiesFromNetwork(propertyId)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            task.result?.documents?.forEach { document ->
                                document.toObject(Amenity::class.java)?.let { newAmenities.add(it) }
                            }
                        }
                    }
            networkOperations.add(amenitiesDownload)

            val addressDownload = propertyRepository.getAddressFromNetwork(propertyId)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            task.result?.toObject(Address::class.java)?.let{ newAddresses.add(it) }
                        }
                    }

            networkOperations.add(addressDownload)

            val picturesDownload = propertyRepository.getPicturesFromNetwork(propertyId)
                    .addOnCompleteListener {task ->
                        if(task.isSuccessful){
                            task.result?.documents?.forEach { document ->
                                document.toObject(Picture::class.java)?.let { newPictures.add(it) }
                            }

                        }
                    }
            networkOperations.add(picturesDownload)
        }

        Tasks.whenAll(networkOperations).addOnCompleteListener {
            if(it.isSuccessful){
                getDataFromStorage(context)
            } else emitResultNetworkRequestFailure(ERROR_FETCHING_NEW_FROM_NETWORK)

        }
    }

    private fun getDataFromStorage(context: Context){
        val storageOperation = mutableListOf<Task<*>>()
        newAgents.forEach { agent ->
            agent.urlProfilePicture?.let {
                val tempFile = filePathToInternalStorage(context, generateName(), TypeImage.AGENT)
                val picAgentDownload = agentRepository.getReferenceAgentPicture(agent.id).getFile(tempFile)
                        .addOnCompleteListener { storageTask ->
                            agent.urlProfilePicture = if(storageTask.isSuccessful){
                                tempFile.absolutePath
                            } else null

                        }
                storageOperation.add(picAgentDownload)
            }
        }
        newPictures.forEach { picture ->
            val tempFilePicture = createImageFileInExtStorage()
            val pictureDownload = propertyRepository.getPictureStorageReference(picture.id).getFile(tempFilePicture)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            picture.url = tempFilePicture.absolutePath
                            addPictureToGallery(context, tempFilePicture.absolutePath)
                        }
                    }
            storageOperation.add(pictureDownload)
            picture.thumbnailUrl?.let { _ ->
                val tempFileThumbnail = filePathToInternalStorage(
                        context, generateName(), TypeImage.PROPERTY
                )
                val thumbnailDownload = propertyRepository.getThumbnailStorageReference(picture.id).getFile(tempFileThumbnail)
                        .addOnCompleteListener{ storageTask ->
                            picture.thumbnailUrl = if(storageTask.isSuccessful) {
                                tempFileThumbnail.absolutePath
                            } else null

                        }
                storageOperation.add(thumbnailDownload)

            }

        }

        newAddresses.forEach { address ->
            val tempFileMap = filePathToInternalStorage(context, address.propertyId, TypeImage.ICON_MAP)
            val mapDownload = propertyRepository.getMapStorageReference(address.propertyId).getFile(tempFileMap)
                    .addOnCompleteListener{ taskStorage->
                        if(taskStorage.isSuccessful) {
                            address.mapIconUrl = tempFileMap.absolutePath
                        }
                    }
            storageOperation.add(mapDownload)

        }

        Tasks.whenAll(storageOperation).addOnCompleteListener {
            if(!it.isSuccessful) emitResultNetworkRequestFailure(ERROR_DOWNLOADING_IMAGES)
            createNewDataInDBLocally()
            saveDataRepository.lastUpdateFromNetwork = todaysDate

        }
    }

    private fun emitResultNetworkRequestFailure(error: ErrorSourceMainActivity){
        val result: Lce<MainActivityResult> = Lce.Error(
                MainActivityResult.UpdateDataFromNetwork(error, null)
        )
        resultToViewState(result)

    }

    private fun createNewDataInDBLocally(){
        if(createPropertiesAndDataJob?.isActive == true) createPropertiesAndDataJob?.cancel()
        createPropertiesAndDataJob = launch {
            agentRepository.createAllNewAgents(newAgents)
            propertyRepository.createDownloadedDataLocally(newProperty, newAddresses, newPictures, newAmenities)

            val result: Lce<MainActivityResult> = Lce.Content(
                    MainActivityResult.UpdateDataFromNetwork(null, newProperty.size)
            )
            resultToViewState(result)
        }
    }

}

