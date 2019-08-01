package com.openclassrooms.realestatemanager.data

import com.openclassrooms.realestatemanager.data.database.dao.AddressDao
import com.openclassrooms.realestatemanager.data.database.dao.AmenityDao
import com.openclassrooms.realestatemanager.data.database.dao.PictureDao
import com.openclassrooms.realestatemanager.data.database.dao.PropertyDao
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property

/**
 * Created by galou on 2019-07-05
 */
class PropertyRepository(
        private val propertyDao: PropertyDao,
        private val amenityDao: AmenityDao,
        private val pictureDao: PictureDao,
        private val addressDao: AddressDao){

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
}