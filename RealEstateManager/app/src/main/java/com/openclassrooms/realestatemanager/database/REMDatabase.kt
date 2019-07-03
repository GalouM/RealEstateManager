package com.openclassrooms.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.openclassrooms.realestatemanager.database.dao.*
import com.openclassrooms.realestatemanager.models.*

/**
 * Created by galou on 2019-07-03
 */
@Database(entities = [Address::class, Agent::class, Amenity::class, Picture::class, Property::class], version = 1)
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
        fun getDatabase(context: Context): REMDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        REMDatabase::class.java,
                        "REM_database.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
