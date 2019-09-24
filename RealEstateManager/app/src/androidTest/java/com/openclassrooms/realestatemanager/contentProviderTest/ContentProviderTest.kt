package com.openclassrooms.realestatemanager.contentProviderTest

import android.content.ContentResolver
import android.util.Log
import androidx.core.net.toUri
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.data.database.REMDatabase
import com.openclassrooms.realestatemanager.provider.PropertyProvider
import com.openclassrooms.realestatemanager.utils.idGenerated
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by galou on 2019-09-24
 */
@RunWith(AndroidJUnit4::class)
class ContentProviderTest {

    private lateinit var db: REMDatabase
    private lateinit var contentResolver: ContentResolver


    private val idAgent = idGenerated
    private val uriAgent = "${PropertyProvider.uriAgent}/$idAgent".toUri()
    private val idProperty = idGenerated
    private val uriProperty = "${PropertyProvider.uriProperty}/$idProperty".toUri()

    @Before
    fun setup(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        db = Room.inMemoryDatabaseBuilder(context, REMDatabase::class.java)
                .allowMainThreadQueries()
                .build()

        contentResolver = context.contentResolver
    }

    @Test
    fun getPropertyWhenNoItemInserted(){
        val uriProperty = "${PropertyProvider.uriProperty}/$idProperty".toUri()
        val cursor = contentResolver.query(uriProperty, null, null, null, null)
        assertNotNull(cursor)
        assertEquals(0, cursor?.count)
        cursor?.close()
    }

    @Test
    fun getAgentWhenNoItemInserted(){
        val uriAgent = "${PropertyProvider.uriAgent}/$idAgent".toUri()
        val cursor = contentResolver.query(uriAgent, null, null, null, null)
        assertNotNull(cursor)
        assertEquals(0, cursor?.count)
        cursor?.close()
    }

    @Test
    fun insertAndGetAgent(){
        contentResolver.insert(uriAgent, generateAgentContentValue(idAgent))
        val cursor = contentResolver.query(uriAgent, null, null, null, null)
        assertNotNull(cursor)
        assertEquals(1, cursor?.count)
        assertTrue(cursor!!.moveToFirst())
        assertEquals(idAgent, cursor.getString(cursor.getColumnIndexOrThrow("agent_id")))
    }

    @Test
    fun insertAndGetNewProperty(){
        contentResolver.insert(uriAgent, generateAgentContentValue(idAgent))
        contentResolver.insert(uriProperty, generatePropertyAndDataContentValue(idProperty, idAgent))
        val cursorProperty = contentResolver.query(uriProperty, null, null, null, null)
        assertNotNull(cursorProperty)
        assertEquals(1, cursorProperty?.count)
        assertTrue(cursorProperty!!.moveToFirst())
        assertEquals(idProperty, cursorProperty.getString(cursorProperty.getColumnIndexOrThrow("property_id")))

    }



}
