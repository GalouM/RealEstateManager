package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.utils.AGENT_TABLE_NAME
import com.openclassrooms.realestatemanager.utils.AUTHORITY
import com.openclassrooms.realestatemanager.utils.PROPERTY_TABLE_NAME
import com.openclassrooms.realestatemanager.utils.URI_PATH
import java.lang.Exception

/**
 * Created by galou on 2019-09-22
 */

class PropertyProvider : ContentProvider() {

    val uriProperty = String.format(URI_PATH, PROPERTY_TABLE_NAME).toUri()

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        if(context != null && contentValues != null){
            val property = propertyFromContentValues(contentValues)
            val address = addressFromContentValues(contentValues)
            val listPictures = pictureFromContentValues(contentValues)
            val listAmenities = amenityFromContentValues(contentValues)
            val database = REMDatabase.getDatabase(context)
        }

        throw Exception("Impossible to insert new data")
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        context?.let{ context ->
            val propertyId = ContentUris.parseId(uri).toString()
            val cursor = REMDatabase.getDatabase(context).propertyDao().getPropertyWithCursor(propertyId)
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        }

        throw Exception("Failed to query row for uri $uri")
    }

    override fun onCreate(): Boolean = true

    override fun update(uri: Uri, contentValues: ContentValues?, p2: String?, p3: Array<String>?): Int {
        throw Exception("Impossible to update data")
    }

    override fun delete(uri: Uri, p1: String?, p2: Array<String>?): Int {
        throw Exception("Impossible to delete data")
    }

    override fun getType(p0: Uri): String? = "vnd.android.cursor.item/$AUTHORITY.$PROPERTY_TABLE_NAME"
}