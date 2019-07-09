package com.openclassrooms.realestatemanager.data.database

import android.content.Context
import androidx.room.*
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.data.database.dao.*

/**
 * Created by galou on 2019-07-03
 */
@Database(entities = [Address::class, Agent::class, Amenity::class, Picture::class, Property::class], version = 1)
@TypeConverters(Converters::class)
public abstract class REMDatabase : RoomDatabase(){

    // --- DAO
    abstract fun addressDao(): AddressDao
    abstract fun agentDao(): AgentDao
    abstract fun amenityDao(): AmenityDao
    abstract fun pictureDao(): PictureDao
    abstract fun propertyDao(): PropertyDao

    companion object{
        @Volatile
        private var INSTANCE: REMDatabase? = null
        fun getDatabase(context: Context): REMDatabase {
            return INSTANCE
                    ?: synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        REMDatabase::class.java,
                        "REM_database.db")
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}