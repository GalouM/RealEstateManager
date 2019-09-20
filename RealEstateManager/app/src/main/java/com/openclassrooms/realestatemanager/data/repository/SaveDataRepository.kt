package com.openclassrooms.realestatemanager.data.repository

import android.content.Context
import com.google.gson.Gson
import com.openclassrooms.realestatemanager.data.TempProperty
import com.openclassrooms.realestatemanager.utils.KEY_PREF
import com.openclassrooms.realestatemanager.utils.KEY_PREF_LAST_UPDATE
import com.openclassrooms.realestatemanager.utils.KEY_PREF_TEMP_PROPERTY
import com.openclassrooms.realestatemanager.utils.extensions.toDateWithTime
import com.openclassrooms.realestatemanager.utils.extensions.toStringWithTime
import java.util.*

/**
 * Created by galou on 2019-09-09
 */
class SaveDataRepository (context: Context){

    private val sharedPreferences = context.getSharedPreferences(KEY_PREF, Context.MODE_PRIVATE)

    var lastUpdateFromNetwork: Date? = null
        get(){
            val dateSaved = sharedPreferences.getString(KEY_PREF_LAST_UPDATE, null)
            dateSaved?.let{ field = it.toDateWithTime()}
            return field
        }
        set(value){
            val editor = sharedPreferences.edit()
            val dateToString = value?.toStringWithTime()
            editor.putString(KEY_PREF_LAST_UPDATE, dateToString)
            editor.apply()
            field = value
        }

    var tempProperty: TempProperty? = null
        get(){
            if(field == null) {
                val gson = Gson()
                val json = sharedPreferences.getString(KEY_PREF_TEMP_PROPERTY, null)
                field = gson.fromJson(json, TempProperty::class.java)
            }
            return field
        }
        set(value){
            val gson = Gson()
            val json = gson.toJson(value)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_PREF_TEMP_PROPERTY, json)
            editor.apply()
            field = value
        }

    fun saveModifiedProperty(modifiedProperty: TempProperty?, idProperty: String){
        val gson = Gson()
        val json = gson.toJson(modifiedProperty)
        val editor = sharedPreferences.edit()
        editor.putString(getKeyProperty(idProperty), json)
        editor.apply()
    }

    fun getSavedModifyProperty(idProperty: String?): TempProperty?{
        val gson = Gson()
        val json = sharedPreferences.getString(getKeyProperty(idProperty), null)
        return gson.fromJson(json, TempProperty::class.java)
    }

    private fun getKeyProperty(idProperty: String?)
            = if(idProperty!= null) "${KEY_PREF_TEMP_PROPERTY}_$idProperty" else KEY_PREF_TEMP_PROPERTY


    companion object{
        @Volatile
        private var INSTANCE: SaveDataRepository? = null
        fun getSaveDataRepository(context: Context): SaveDataRepository {
            return INSTANCE
                    ?: synchronized(this){
                        val instance = SaveDataRepository(context)
                        INSTANCE = instance
                        return instance
                    }
        }
    }
}