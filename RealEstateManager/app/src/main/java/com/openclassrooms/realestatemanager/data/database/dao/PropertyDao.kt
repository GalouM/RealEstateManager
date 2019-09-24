package com.openclassrooms.realestatemanager.data.database.dao

import android.database.Cursor
import androidx.room.*
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.utils.*
import java.util.*

/**
 * Created by galou on 2019-07-03
 */

@Dao
abstract class PropertyDao(private val database: REMDatabase) {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createProperty(property: Property)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun createProperties(properties: List<Property>)

    @Update
    abstract suspend fun updateProperty(property: Property)

    @Query("SELECT * FROM $PROPERTY_TABLE_NAME WHERE property_id = :propertyId")
    abstract suspend fun getProperty(propertyId: String): List<PropertyWithAllData>

    @Query("SELECT * FROM $PROPERTY_TABLE_NAME ORDER BY on_market_since")
    abstract suspend fun getAllProperties(): List<PropertyWithAllData>

    @Query("SELECT * FROM $PROPERTY_TABLE_NAME " +
            "INNER JOIN $ADDRESS_TABLE_NAME ON $ADDRESS_TABLE_NAME.address_id = $PROPERTY_TABLE_NAME.property_id WHERE " +
            "$ADDRESS_TABLE_NAME.neighbourhood LIKE :neighborhood " +
            "AND ($PROPERTY_TABLE_NAME.price BETWEEN :minPrice AND :maxPrice) " +
            "AND ($PROPERTY_TABLE_NAME.surface BETWEEN :minSurface AND :maxSurface) " +
            "AND ($PROPERTY_TABLE_NAME.rooms >= :minNbRoom) " +
            "AND ($PROPERTY_TABLE_NAME.bedrooms >= :minNbBedrooms OR bedrooms IS NULL) " +
            "AND ($PROPERTY_TABLE_NAME.bathrooms >= :minNbBathrooms OR bathrooms IS NULL) " +
            "AND ($PROPERTY_TABLE_NAME.agent IN (:listAgents)) " +
            "AND ($PROPERTY_TABLE_NAME.type_property IN (:listTypes)) " +
            "AND ($PROPERTY_TABLE_NAME.sold IN (:isSold)) " +
            "AND ($PROPERTY_TABLE_NAME.has_picture IN (:hasPicture)) " +
            "AND ($PROPERTY_TABLE_NAME.on_market_since >= :afterDate) " +
            "ORDER BY on_market_since")
    abstract suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double,
            minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<String>, listTypes: List<TypeProperty>, neighborhood: String,
            isSold: List<Int>, hasPicture: List<Int>, afterDate: Date
    ): List<PropertyWithAllData>

    @Query("SELECT * FROM $PROPERTY_TABLE_NAME " +
            "INNER JOIN $AMENITY_TABLE_NAME ON $AMENITY_TABLE_NAME.property = $PROPERTY_TABLE_NAME.property_id " +
            "INNER JOIN $ADDRESS_TABLE_NAME ON $ADDRESS_TABLE_NAME.address_id = $PROPERTY_TABLE_NAME.property_id WHERE " +
            "(amenities.type_amenity IN (:listAmenities)) " +
            "AND (address.neighbourhood LIKE :neighborhood) " +
            "AND ($PROPERTY_TABLE_NAME.price BETWEEN :minPrice AND :maxPrice)" +
            "AND ($PROPERTY_TABLE_NAME.surface BETWEEN :minSurface AND :maxSurface) " +
            "AND ($PROPERTY_TABLE_NAME.rooms >= :minNbRoom) " +
            "AND ($PROPERTY_TABLE_NAME.bedrooms >= :minNbBedrooms OR bedrooms IS NULL) " +
            "AND ($PROPERTY_TABLE_NAME.bathrooms >= :minNbBathrooms OR bathrooms IS NULL) " +
            "AND ($PROPERTY_TABLE_NAME.agent IN (:listAgents)) " +
            "AND ($PROPERTY_TABLE_NAME.type_property IN (:listTypes)) " +
            "AND ($PROPERTY_TABLE_NAME.sold IN (:isSold)) " +
            "AND ($PROPERTY_TABLE_NAME.has_picture IN (:hasPicture)) " +
            "AND ($PROPERTY_TABLE_NAME.on_market_since >= :afterDate) " +
            "ORDER BY on_market_since")
    abstract suspend fun getPropertiesQuery(
            minPrice: Double, maxPrice: Double,
            minSurface: Double, maxSurface: Double,
            minNbRoom: Int, minNbBedrooms: Int, minNbBathrooms: Int,
            listAgents: List<String>, listTypes: List<TypeProperty>, neighborhood: String,
            isSold: List<Int>, hasPicture: List<Int>, afterDate: Date, listAmenities: List<TypeAmenity>
    ): List<PropertyWithAllData>


    @Transaction
    open suspend fun createPropertyAndData(
            property: Property, address: Address, pictures: List<Picture>, amenities: List<Amenity>
    ){
        createProperty(property)
        database.addressDao().createAddress(address)
        database.pictureDao().insertPicture(pictures)
        database.amenityDao().insertAmenity(amenities)
    }

    @Transaction
    open suspend fun updatePropertyAndData(
            property: Property, address: Address, newPictures: List<Picture>, amenities: List<Amenity>,
            picturesToDelete: List<Picture>, picturesToUpdate: List<Picture>, amenitiesToDelete: List<Amenity>
    ){
        updateProperty(property)
        database.addressDao().updateAddress(address)
        database.pictureDao().deletePictures(picturesToDelete.map { it.id })
        database.pictureDao().updatePicture(picturesToUpdate)
        database.pictureDao().insertPicture(newPictures)
        database.amenityDao().deleteAmenities(amenitiesToDelete.map { it.id })
        database.amenityDao().insertAmenity(amenities)
    }

    @Transaction
    open suspend fun createPropertiesAndData(
            properties: List<Property>, addresses: List<Address>, pictures: List<Picture>, amenities: List<Amenity>
    ){
        createProperties(properties)
        database.addressDao().createAddresses(addresses)
        database.pictureDao().insertPicture(pictures)
        database.amenityDao().insertAmenity(amenities)
    }



    @Query("SELECT * FROM $PROPERTY_TABLE_NAME ORDER BY on_market_since")
    abstract  fun getAllPropertiesWithCursor(): Cursor

    @Query("SELECT * FROM $PROPERTY_TABLE_NAME WHERE property_id = :propertyId")
    abstract fun getPropertyWithCursor(propertyId: String): Cursor

}