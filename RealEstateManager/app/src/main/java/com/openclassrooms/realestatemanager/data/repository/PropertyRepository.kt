package com.openclassrooms.realestatemanager.data.repository

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

    //-----------------
    // API ADDRESS REQUEST
    //-----------------

    fun getLocationFromAddress(address: String): Observable<GeocodingApiResponse>{
        return geocodingApiService.getLocationFromAddress(address, BuildConfig.GoogleMapApiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
    }

    fun getMapLocation(lat: String, lng: String): String{
        return "${BASE_URL_MAP_API}staticmap?zoom=${MAP_ICON_ZOOM}&size=${MAP_ICON_SIZE}x${MAP_ICON_SIZE}&maptype=roadmap&markers=color:${MAP_ICON_MARKER_COLOR}%7C${lat},${lng}&key=${BuildConfig.GoogleMapApiKey}"
    }

    //-----------------
    // DB REQUEST
    //-----------------
    //------get--------
    suspend fun getAllProperties(): List<PropertyWithAllData>{
        return propertyDao.getAllProperties()
    }

    suspend fun getProperty(idProperty: Int): List<PropertyWithAllData>{
        return propertyDao.getProperty(idProperty)
    }

    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double, minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<Int>, listTypes: List<TypeProperty>, neighborhood: String,
            isSold: List<Int>, hasPictures: List<Int>, afterDate: Date
    ): List<PropertyWithAllData>{
        return propertyDao.getPropertiesQuery(
                minPrice, maxPrice, minSurface, maxSurface,
                minNbRoom, minNbBedrooms, minNbBathrooms,
                listAgents, listTypes, neighborhood, isSold, hasPictures, afterDate
        )
    }

    suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double, minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<Int>, listTypes: List<TypeProperty>, neighborhood: String,
            isSold: List<Int>, hasPictures: List<Int>, afterDate: Date, listAmenities: List<TypeAmenity>
    ): List<PropertyWithAllData>{
        return propertyDao.getPropertiesQuery(
                minPrice, maxPrice, minSurface, maxSurface,
                minNbRoom, minNbBedrooms, minNbBathrooms,
                listAgents, listTypes, neighborhood, isSold, hasPictures, afterDate, listAmenities
        )
    }

    //------create--------
    suspend fun createProperty(property: Property): Long{
        return propertyDao.createProperty(property)
    }

    suspend fun createDataProperty(
            amenities: List<Amenity>, pictures: List<Picture>, address: Address, actionType: ActionType
    ){
        coroutineScope {
            launch {
                amenityDao.insertAmenity(amenities)
            }
            launch {
                pictureDao.insertPicture(pictures)
            }
            launch {
                when(actionType){
                    ActionType.NEW_PROPERTY -> addressDao.createAddress(address)
                    ActionType.MODIFY_PROPERTY -> addressDao.updateAddress(address)
                }

            }
        }
    }

    suspend fun updateProperty(property: Property){
        propertyDao.updateProperty(property)
    }

    //------delete--------
    suspend fun deletePreviousData(idProperty: Int){
        amenityDao.deleteAmenities(idProperty)
        pictureDao.deletePictures(idProperty)
    }



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