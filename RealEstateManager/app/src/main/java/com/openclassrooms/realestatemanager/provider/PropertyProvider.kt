package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.utils.*
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.lang.IllegalArgumentException

/**
 * Created by galou on 2019-09-22
 */

class PropertyProvider : ContentProvider() {

    companion object{
        val uriProperty = "$URI_PATH/$PROPERTY_TABLE_NAME"
        val uriAgent = "$URI_PATH/$AGENT_TABLE_NAME"
    }


    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, PROPERTY_TABLE_NAME, CODE_PROPERTY_DIR)
        addURI(AUTHORITY, "$PROPERTY_TABLE_NAME/*", CODE_PROPERTY_ITEM)
        addURI(AUTHORITY, "$PROPERTY_TABLE_NAME/*/address", CODE_PROPERTY_ADDRESS)
        addURI(AUTHORITY, "$PROPERTY_TABLE_NAME/*/$PICTURE_TABLE_NAME", CODE_PROPERTY_PICTURES)
        addURI(AUTHORITY, "$PROPERTY_TABLE_NAME/*/$AMENITY_TABLE_NAME", CODE_PROPERTY_AMENITIES)
        addURI(AUTHORITY, "$AMENITY_TABLE_NAME/*", CODE_AMENITY_ITEM)
        addURI(AUTHORITY, "$PICTURE_TABLE_NAME/*", CODE_PICTURE_ITEM)
        addURI(AUTHORITY, AGENT_TABLE_NAME, CODE_AGENT_DIR)
        addURI(AUTHORITY, "$AGENT_TABLE_NAME/*", CODE_AGENT_ITEM)
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        if(context != null && contentValues != null) {
            val database = REMDatabase.getDatabase(context!!)
            when (uriMatcher.match(uri)) {
                CODE_PROPERTY_ITEM -> {
                    val property = propertyFromContentValues(contentValues)
                    val address = addressFromContentValues(contentValues)
                    val listPictures = pictureFromContentValues(contentValues)
                    val listAmenities = amenityFromContentValues(contentValues)
                    runBlocking {
                        database.propertyDao().createPropertyAndData(property, address, listPictures, listAmenities)
                        context!!.contentResolver.notifyChange(uri, null)
                    }
                    return "$uri/${property.id}".toUri()
                }
                CODE_PROPERTY_DIR -> throw IllegalArgumentException("Invalid URI cannot insert without an ID $uri")
                CODE_PICTURE_ITEM -> throw IllegalArgumentException("Invalid URI cannot create data without a property $uri")
                CODE_AMENITY_ITEM -> throw IllegalArgumentException("Invalid URI cannot create data without a property $uri")
                CODE_PROPERTY_ADDRESS -> throw IllegalArgumentException("Invalid URI cannot create data without a property $uri")
                CODE_AGENT_DIR -> throw IllegalArgumentException("Invalid URI cannot insert without an ID $uri")
                CODE_AGENT_ITEM -> {
                    val agent = agentFromContentValues(contentValues)
                    runBlocking {
                        database.agentDao().createAgent(agent)
                        context!!.contentResolver.notifyChange(uri, null)
                    }
                    return "$uri/${agent.id}".toUri()
                }
                else -> throw IllegalArgumentException("Unknown URI $uri")
            }
        }
        throw Exception("Failed to insert row into $uri")

    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val id = uri.lastPathSegment
        if(id != null && context != null){
            val database = REMDatabase.getDatabase(context!!)
           return when(uriMatcher.match(uri)){
                CODE_PICTURE_ITEM -> database.pictureDao().getPictureWithCursor(id)
                CODE_AMENITY_ITEM -> database.amenityDao().getAmenityWithCursor(id)
                CODE_PROPERTY_AMENITIES -> database.amenityDao().getPropertyAmenitiesWithCursor(id)
                CODE_PROPERTY_PICTURES -> database.pictureDao().getPropertyPicturesWithCursor(id)
                CODE_PROPERTY_ADDRESS -> {
                    val idAddress = uri.pathSegments[1]
                    database.addressDao().getAddressWithCursor(idAddress)
                }
                CODE_PROPERTY_ITEM -> database.propertyDao().getPropertyWithCursor(id)
                CODE_PROPERTY_DIR -> database.propertyDao().getAllPropertiesWithCursor()
                CODE_AGENT_DIR -> database.agentDao().getAllAgentsWithCursor()
                CODE_AGENT_ITEM -> database.agentDao().getAgentWithCursor(id)
               else -> throw IllegalArgumentException("Query doesn't exist $uri")
            }
        }

        throw Exception("Failed to query row for uri $uri")
    }

    override fun onCreate(): Boolean = true

    override fun update(uri: Uri, contentValues: ContentValues?, p2: String?, p3: Array<String>?): Int {

        throw IllegalArgumentException("Impossible to update $uri")
    }

    override fun delete(uri: Uri, p1: String?, p2: Array<String>?): Int {
        throw IllegalArgumentException("Impossible to delete data")
    }

    override fun getType(uri: Uri): String? = when(uriMatcher.match(uri)){
        CODE_PICTURE_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$PICTURE_TABLE_NAME"
        CODE_AMENITY_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$AMENITY_TABLE_NAME"
        CODE_PROPERTY_AMENITIES -> "vnd.android.cursor.dir/$AUTHORITY.$AMENITY_TABLE_NAME"
        CODE_PROPERTY_PICTURES -> "vnd.android.cursor.dir/$AUTHORITY.$PICTURE_TABLE_NAME"
        CODE_PROPERTY_ADDRESS -> "vnd.android.cursor.item/$AUTHORITY.$ADDRESS_TABLE_NAME"
        CODE_PROPERTY_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$PROPERTY_TABLE_NAME"
        CODE_PROPERTY_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$PROPERTY_TABLE_NAME"
        CODE_AGENT_DIR -> "vnd.android.cursor.dir/$AUTHORITY.$AGENT_TABLE_NAME"
        CODE_AGENT_ITEM -> "vnd.android.cursor.item/$AUTHORITY.$AGENT_TABLE_NAME"
        else -> throw IllegalArgumentException("Unknown URI: $uri")
    }
}