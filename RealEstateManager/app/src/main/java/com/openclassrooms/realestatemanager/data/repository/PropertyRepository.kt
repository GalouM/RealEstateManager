package com.openclassrooms.realestatemanager.data.repository

import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.data.api.GeocodingApiService
import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApiResponse
import com.openclassrooms.realestatemanager.data.database.dao.AddressDao
import com.openclassrooms.realestatemanager.data.database.dao.AmenityDao
import com.openclassrooms.realestatemanager.data.database.dao.PictureDao
import com.openclassrooms.realestatemanager.data.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.utils.BASE_URL_MAP_API
import com.openclassrooms.realestatemanager.utils.MAP_ICON_MARKER_COLOR
import com.openclassrooms.realestatemanager.utils.MAP_ICON_SIZE
import com.openclassrooms.realestatemanager.utils.MAP_ICON_ZOOM
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by galou on 2019-07-05
 */
class PropertyRepository(
        private val propertyDao: PropertyDao,
        private val amenityDao: AmenityDao,
        private val pictureDao: PictureDao,
        private val addressDao: AddressDao,
        private val geocodingApiService: GeocodingApiService){

    private var idPropertyPicked: Int? = null

    suspend fun createProperty(property: Property): Long{
        return propertyDao.createProperty(property)
    }

    suspend fun updateProperty(property: Property){
        propertyDao.updateProperty(property)
    }

    suspend fun insertAmenity(amenity: Amenity){
        amenityDao.insertAmenity(amenity)
    }

    suspend fun deleteAmenity(id: Int){
        amenityDao.deleteAmenity(id)
    }

    suspend fun insertPicture(picture: Picture){
        pictureDao.insertPicture(picture)
    }

    suspend fun deletePicture(url: String){
        pictureDao.deletePicture(url)
    }

    suspend fun createAddress(address: Address): Long{
        return addressDao.createAddress(address)
    }

    suspend fun updateAddress(address: Address){
        addressDao.updateAddress(address)
    }

    fun getLocationFromAddress(address: String): Observable<GeocodingApiResponse>{
        return geocodingApiService.getLocationFromAddress(address, BuildConfig.GoogleMapApiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
    }

    fun getMapLocation(lat: String, lng: String): String{
        return "${BASE_URL_MAP_API}staticmap?zoom=${MAP_ICON_ZOOM}&size=${MAP_ICON_SIZE}x${MAP_ICON_SIZE}&maptype=roadmap&markers=color:${MAP_ICON_MARKER_COLOR}%7C${lat},${lng}&key=${BuildConfig.GoogleMapApiKey}"
    }

    suspend fun getAllProperties(): List<Property>{
        return propertyDao.getAllProperties()
    }

    suspend fun getProperty(idProperty: Int): List<Property>{
        return propertyDao.getProperty(idProperty)
    }

    suspend fun getPropertyAddress(idProperty: Int): List<Address>{
        return addressDao.getAddress(idProperty)
    }

    suspend fun getPropertyPicture(idProperty: Int): List<Picture>{
        return pictureDao.getPictures(idProperty)
    }

    suspend fun getPropertyAmenities(idProperty: Int): List<Amenity>{
        return amenityDao.getAmenities(idProperty)
    }

    fun setIdPropertyPicked(id: Int){
        idPropertyPicked = id
    }

    fun getPropertyPickedId(): Int?{
        return idPropertyPicked
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