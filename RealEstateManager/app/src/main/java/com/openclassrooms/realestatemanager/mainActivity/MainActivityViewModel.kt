package com.openclassrooms.realestatemanager.mainActivity

import android.content.Context
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FileDownloadTask
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
    private val latestUpdate = saveDataRepository.lastUpdateFromNetwork

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
                                isOpenAddProperty = true,
                                errorSource = null,
                                isLoading = false)
                    }
                    is MainActivityResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                isOpenAddProperty = false,
                                currency = result.packet.currency
                                )
                    }
                    is MainActivityResult.UpdataDataFromNetwork -> {
                        currentViewState.copy(
                                isOpenAddProperty = false,
                                errorSource = null,
                                isLoading = false,
                                newDataUploaded = true
                        )
                    }
                }
            }

            is Lce.Loading -> {
                currentViewState.copy(
                        isLoading = true,
                        errorSource = null,
                        isOpenAddProperty = false)
            }
            is Lce.Error -> {
                when(result.packet){
                    is MainActivityResult.OpenAddPropertyResult -> {
                        currentViewState.copy(
                                isOpenAddProperty = false,
                                errorSource = NO_AGENT_IN_DB,
                                isLoading = false
                        )
                    }
                    is MainActivityResult.ChangeCurrencyResult -> {
                        currentViewState.copy(
                                isOpenAddProperty = false
                        )
                    }
                    is MainActivityResult.UpdataDataFromNetwork -> {
                        currentViewState.copy(
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

        displayData("last update : $latestUpdate")

        propertyRepository.getAllPropertiesFromNetwork(latestUpdate)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        task.result?.documents?.forEach { document ->
                            document.toObject(Property::class.java)?.let { newProperty.add(it) }
                        }
                        getDataAndAgent(context)
                    } else emitResultNetworkRequestFailure()
                }


    }

    private fun getDataAndAgent(context: Context){
        val networkOperations = mutableListOf<Task<*>>()

        val agentsDownload = agentRepository.getAgentsFromNetwork(latestUpdate)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.documents?.forEach { document ->
                            val agent = document.toObject(Agent::class.java)
                            agent?.let {
                                displayData("pic before ${agent.urlProfilePicture}")
                                agent.urlProfilePicture?.let {
                                    val tempFile = filePathToInternalStorage(context, generateName(), TypeImage.AGENT)
                                    val picAgentDownload = agentRepository.getReferenceAgentPicture(agent.id).getFile(tempFile)
                                            .addOnCompleteListener { storageTask ->
                                                displayData("${storageTask.isSuccessful}")
                                                agent.urlProfilePicture = if(storageTask.isSuccessful){
                                                    tempFile.absolutePath
                                                } else null
                                                displayData("pic after ${agent.urlProfilePicture}")

                                            }
                                    networkOperations.add(picAgentDownload)
                                }
                                newAgents.add(it)
                            }
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
                            displayData("address fetched")
                            val address: Address? = task.result?.toObject(Address::class.java)
                            address?.let {
                                newAddresses.add(it)
                                val tempFileMap = filePathToInternalStorage(context, generateName(), TypeImage.ICON_MAP)
                                val mapDownload = propertyRepository.getMapStorageReference(it.propertyId).getFile(tempFileMap)
                                        .addOnCompleteListener{ taskStorage->
                                            displayData("map fetched")
                                            if(taskStorage.isSuccessful) {
                                                it.mapIconUrl = tempFileMap.absolutePath
                                            }
                                        }
                                networkOperations.add(mapDownload)
                            }
                        }
                    }

            networkOperations.add(addressDownload)
            val picturesDownload = propertyRepository.getPicturesFromNetwork(propertyId)
                    .addOnCompleteListener {task ->
                        if(task.isSuccessful){
                            displayData("picture fetched form FB")
                            task.result?.documents?.forEach { document ->
                                val picture = document.toObject(Picture::class.java)
                                picture?.let {
                                    newPictures.add(it)
                                    val tempFilePicture = createImageFileInExtStorage()
                                    val pictureDownload = propertyRepository.getPictureStorageReference(it.id).getFile(tempFilePicture)
                                            .addOnCompleteListener{ task ->
                                                displayData("picture fetched from storage")
                                                if(task.isSuccessful){
                                                    it.url = tempFilePicture.absolutePath
                                                    addPictureToGallery(context, tempFilePicture.absolutePath)
                                                }
                                            }
                                    networkOperations.add(pictureDownload)
                                    it.thumbnailUrl?.let { _ ->
                                        val tempFileThumbnail = filePathToInternalStorage(
                                                context, generateName(), TypeImage.PROPERTY
                                        )
                                        val thumbnailDownload = propertyRepository.getThumbnailStorageReference(it.id).getFile(tempFileThumbnail)
                                                .addOnCompleteListener{ storageTask ->
                                                    it.thumbnailUrl = if(storageTask.isSuccessful) {
                                                        tempFileThumbnail.absolutePath
                                                    } else null

                                                }
                                        networkOperations.add(thumbnailDownload)

                                    }

                                }
                            }

                        }
                    }
            networkOperations.add(picturesDownload)
        }

        Tasks.whenAll(networkOperations).addOnCompleteListener {
            if(it.isSuccessful){
                createNewDataInDBLocally()
                saveDataRepository.lastUpdateFromNetwork = todaysDate
            } else emitResultNetworkRequestFailure()

        }


    }

    private fun emitResultNetworkRequestFailure(){
        val result: Lce<MainActivityResult> = Lce.Error(
                MainActivityResult.UpdataDataFromNetwork(ERROR_FETCHING_NEW_FROM_NETWORK)
        )
        resultToViewState(result)

    }

    private fun createNewDataInDBLocally(){
        displayData("list agent: $newAgents")
        displayData("list picture: $newPictures")
        displayData("list address: $newAddresses")
        if(createPropertiesAndDataJob?.isActive == true) createPropertiesAndDataJob?.cancel()
        createPropertiesAndDataJob = launch {
            agentRepository.createAllNewAgents(newAgents)
            propertyRepository.createDownloadedDataLocally(newProperty, newAddresses, newPictures, newAmenities)

            val result: Lce<MainActivityResult> = Lce.Content(MainActivityResult.UpdataDataFromNetwork(null))
            resultToViewState(result)
        }
    }

}

