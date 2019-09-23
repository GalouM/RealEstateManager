package com.openclassrooms.realestatemanager.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.addProperty.ActionType
import com.openclassrooms.realestatemanager.data.api.GeocodingApiService
import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApiResponse
import com.openclassrooms.realestatemanager.data.database.dao.AddressDao
import com.openclassrooms.realestatemanager.data.database.dao.AmenityDao
import com.openclassrooms.realestatemanager.data.database.dao.PictureDao
import com.openclassrooms.realestatemanager.data.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.utils.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by galou on 2019-07-05
 */
class PropertyRepository(
        private val propertyDao: PropertyDao,
        private val amenityDao: AmenityDao,
        private val pictureDao: PictureDao,
        private val addressDao: AddressDao,
        private val geocodingApiService: GeocodingApiService
){

    var propertyPicked: PropertyWithAllData? = null

    var propertyFromSearch: List<PropertyWithAllData>? = null

    suspend fun createPropertyAndData(
            property: Property, newPictures: List<Picture>, picturesToDelete: List<Picture>, picturesToUpdate: List<Picture>,
            amenities: List<Amenity>, address: Address, actionType: ActionType, amenityToDelete: List<Amenity>
    ): Task<Task<Void>> {
        createPropertyAndDataInLocal(property, amenities, newPictures, picturesToDelete, picturesToUpdate, address, actionType, amenityToDelete)
        return createPropertyAndDataInNetwork(property, newPictures, picturesToDelete, picturesToUpdate, amenities, address, amenityToDelete)
    }

    //-----------------
    // API ADDRESS REQUEST
    //-----------------

    fun getLocationFromAddress(address: String): Observable<GeocodingApiResponse> = geocodingApiService
            .getLocationFromAddress(address, BuildConfig.GoogleMapApiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .timeout(10, TimeUnit.SECONDS)


    fun getMapLocation(lat: String, lng: String): String =  "${BASE_URL_MAP_API}staticmap?zoom=${MAP_ICON_ZOOM}&size=${MAP_ICON_SIZE}x${MAP_ICON_SIZE}&maptype=roadmap&markers=color:${MAP_ICON_MARKER_COLOR}%7C${lat},${lng}&key=${BuildConfig.GoogleMapApiKey}"


    //-----------------
    // LOCAL DB REQUEST
    //-----------------
    //------get--------
    suspend fun getAllProperties() = propertyDao.getAllProperties()


    suspend fun getProperty(idProperty: String) = propertyDao.getProperty(idProperty)

    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double, minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<String>, listTypes: List<TypeProperty>, neighborhood: String,
            isSold: List<Int>, hasPictures: List<Int>, afterDate: Date
    ) = propertyDao.getPropertiesQuery(
            minPrice, maxPrice, minSurface, maxSurface,
            minNbRoom, minNbBedrooms, minNbBathrooms,
            listAgents, listTypes, neighborhood, isSold, hasPictures, afterDate
    )


    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double, minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<String>, listTypes: List<TypeProperty>, neighborhood: String,
            isSold: List<Int>, hasPictures: List<Int>, afterDate: Date, listAmenities: List<TypeAmenity>
    ) = propertyDao.getPropertiesQuery(
            minPrice, maxPrice, minSurface, maxSurface,
            minNbRoom, minNbBedrooms, minNbBathrooms,
            listAgents, listTypes, neighborhood, isSold, hasPictures, afterDate, listAmenities
    )


    //------create--------
    private suspend fun createPropertyAndDataInLocal(
            property: Property, amenities: List<Amenity>, newPictures: List<Picture>, picturesToDelete: List<Picture>, picturesToUpdate: List<Picture>,
            address: Address, actionType: ActionType, amenityToDelete: List<Amenity>
    ){
        coroutineScope {
            launch {
                when(actionType){
                    ActionType.NEW_PROPERTY -> propertyDao.createProperty(property)
                    ActionType.MODIFY_PROPERTY -> propertyDao.updateProperty(property)
                }
            }
            launch { amenityDao.insertAmenity(amenities) }

            launch { pictureDao.updatePicture(picturesToUpdate) }

            launch { pictureDao.insertPicture(newPictures) }

            launch { pictureDao.deletePictures(picturesToDelete.map { it.id }) }

            launch {
                when(actionType){
                    ActionType.NEW_PROPERTY -> addressDao.createAddress(address)
                    ActionType.MODIFY_PROPERTY -> addressDao.updateAddress(address)
                }

            }

            launch { amenityDao.deleteAmenities(amenityToDelete.map{ it.id }) }

        }
    }

    suspend fun createDownloadedDataLocally(
            properties: List<Property>, addresses: List<Address>, pictures: List<Picture>, amenities: List<Amenity>
    ){
        coroutineScope {
            launch { propertyDao.createProperties(properties) }
            launch { addressDao.createAddresses(addresses) }
            launch { pictureDao.insertPicture(pictures) }
            launch { amenityDao.insertAmenity(amenities) }
        }

    }


    //-----------------
    // NETWORK DB REQUEST
    //-----------------
    private val dbNetwork = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val propertyCollection = dbNetwork.collection(PROPERTY_COLLECTION)
    private val pictureCollection = dbNetwork.collection(PICTURE_COLLECTION)
    private val amenityCollection = dbNetwork.collection(AMENITY_COLLECTION)
    private val addressCollection = dbNetwork.collection(ADDRESS_COLLECTION)

    //------create--------
    private fun createPropertyAndDataInNetwork(
            property: Property, newPictures: List<Picture>, picturesToDelete: List<Picture>, picturesToUpdate: List<Picture>
            , amenities: List<Amenity>, address: Address, amenityToDelete: List<Amenity>
    ): Task<Task<Void>>{
        val listOfTaskUploads = mutableListOf<Task<*>>()
        val batch = dbNetwork.batch()

        val propertyRef = propertyCollection.document(property.id)
        batch.set(propertyRef, property)

        val addressRef = addressCollection.document(address.propertyId)
        batch.set(addressRef, address)

        amenities.forEach {
            val amenityRef = amenityCollection.document(it.id)
            batch.set(amenityRef, it)
        }

        amenityToDelete.forEach {
            val amenityRef = amenityCollection.document(it.id)
            batch.delete(amenityRef)
        }

        newPictures.forEach {
            val pictureRef = pictureCollection.document(it.id)
            val uriTask = uploadPictureToStorageAndGetUrl(it)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            it.serverUrl = task.result.toString()
                        }
                        batch.set(pictureRef, it)
                    }
            listOfTaskUploads.add(uriTask)
        }

        picturesToUpdate.forEach {
            val pictureRef = pictureCollection.document(it.id)
            batch.update(pictureRef, "description", it.description,
                    "orderNumber", it.orderNumber)
        }

        picturesToDelete.forEach {
            val pictureRef = pictureCollection.document(it.id)
            val deleteInStorageTask = getPictureStorageReference(it.id).delete()
            listOfTaskUploads.add(deleteInStorageTask)
            batch.delete(pictureRef)
        }

        val uploadAllPictures = Tasks.whenAllComplete(listOfTaskUploads)
        return uploadAllPictures.continueWith { batch.commit() }

    }


    fun uploadMapInNetwork(map: Bitmap, addressId: String): UploadTask {
        val baos = ByteArrayOutputStream()
        map.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        return getMapStorageReference(addressId)
                .putBytes(data)
    }

    private fun uploadPictureToStorageAndGetUrl(picture: Picture): Task<Uri>{
        val uploadPictureTask: UploadTask
        val storageRef = getPictureStorageReference(picture.id)
        uploadPictureTask = if(picture.thumbnailUrl != null){
            val streamPicture = FileInputStream(File(picture.url))
            val streamThumbnail = FileInputStream(File(picture.thumbnailUrl))
            val uploadThumbnail = getThumbnailStorageReference(picture.id).putStream(streamThumbnail)
            storageRef.putStream(streamPicture)
        } else {
            storageRef.putFile(picture.url.toUri())
        }

        return uploadPictureTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if(!task.isSuccessful){
                task.exception?.let{ exception ->
                    throw exception
                }
            }
            return@Continuation storageRef.downloadUrl
        })

    }

    //------get--------
    fun getAllPropertiesFromNetwork(latestUpdate: Date?): Task<QuerySnapshot>{
        return if(latestUpdate != null) {
            propertyCollection
                    .whereGreaterThanOrEqualTo("creationDate", latestUpdate)
                    .get()
        } else {
            propertyCollection.get()
        }
    }

    fun getAddressFromNetwork(idProperty: String): Task<DocumentSnapshot> = addressCollection.document(idProperty).get()

    fun getPicturesFromNetwork(idProperty: String): Task<QuerySnapshot> = pictureCollection
            .whereEqualTo("property", idProperty)
            .get()

    fun getAmenitiesFromNetwork(idProperty: String): Task<QuerySnapshot> = amenityCollection
            .whereEqualTo("property", idProperty)
            .get()

    //------storage reference path--------

    fun getMapStorageReference(addressId: String) = storage.reference
            .child("${STORAGE_PATH_MAP}${addressId}")

    fun getPictureStorageReference(pictureId: String) = storage.reference
            .child("${STORAGE_PATH_PROPERTY_PICTURE}${pictureId}")

    fun getThumbnailStorageReference(pictureId: String) = storage.reference
            .child("${STORAGE_PATH_PROPERTY_PICTURE_THUMBNAIL}${pictureId}")


    companion object{
        @Volatile
        private var INSTANCE: PropertyRepository? = null
        fun getPropertyRepository(propertyDao: PropertyDao,
                                  amenityDao: AmenityDao,
                                  pictureDao: PictureDao,
                                  addressDao: AddressDao,
                                  geocodingApiService: GeocodingApiService): PropertyRepository {
            return INSTANCE
                    ?: synchronized(this){
                        val instance = PropertyRepository(
                                propertyDao, amenityDao, pictureDao, addressDao, geocodingApiService)
                        INSTANCE = instance
                        return instance
                    }
        }
    }





}