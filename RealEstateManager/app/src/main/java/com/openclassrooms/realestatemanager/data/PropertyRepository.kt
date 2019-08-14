package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.data.api.GeocodingApiService
import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApi
import com.openclassrooms.realestatemanager.data.database.dao.AddressDao
import com.openclassrooms.realestatemanager.data.database.dao.AmenityDao
import com.openclassrooms.realestatemanager.data.database.dao.PictureDao
import com.openclassrooms.realestatemanager.data.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import io.reactivex.Observable
import io.reactivex.Scheduler
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

    suspend fun createProperty(property: Property): Long{
        return propertyDao.createProperty(property)
    }

    suspend fun insertAmenity(amenity: Amenity){
        amenityDao.insertAmenity(amenity)
    }

    suspend fun insertPicture(picture: Picture){
        pictureDao.insertPicture(picture)
    }

    suspend fun createAddress(address: Address): Long{
        return addressDao.createAddress(address)
    }

    fun getLocationAndMapFromAddress(address: String): Observable<GeocodingApi>{
        return geocodingApiService.getLocationFromAddress(address)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS)
    }

    suspend fun getAllProperties(): List<Property>{
        return propertyDao.getAllProperties()
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
}