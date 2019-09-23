package com.openclassrooms.realestatemanager.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toUri
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.data.entity.Agent
import com.openclassrooms.realestatemanager.utils.AGENT_TABLE_NAME
import com.openclassrooms.realestatemanager.utils.AUTHORITY
import com.openclassrooms.realestatemanager.utils.URI_PATH
import kotlinx.coroutines.runBlocking
import java.lang.Exception

/**
 * Created by galou on 2019-09-20
 */

class AgentProvider : ContentProvider() {

    private val uriAgent = String.format(URI_PATH, AGENT_TABLE_NAME).toUri()

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        if(context != null && contentValues != null){
            val agent = agentFromContentValues(contentValues)
            runBlocking {
                val id = REMDatabase.getDatabase(context!!).agentDao().createAgent(agent)
                if(id > 0){
                    context!!.contentResolver.notifyChange(uri, null)
                    return@runBlocking ContentUris.withAppendedId(uri, id)
                } else {
                    throw Exception("Impossible to insert new data")
                }
            }
        }

        throw Exception("Impossible to insert new data")
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        context?.let{ context ->
            /*
            val agentId = ContentUris.parseId(uri).toString()
            val cursor = REMDatabase.getDatabase(context).agentDao().getAgentWithCursor(agentId)
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor

             */
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

    override fun getType(p0: Uri): String? = "vnd.android.cursor.item/$AUTHORITY.$AGENT_TABLE_NAME"
}

