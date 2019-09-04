package com.openclassrooms.realestatemanager.data.database

import android.util.Log
import androidx.room.TypeConverter
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty
import java.util.*

/**
 * Created by galou on 2019-07-04
 */

class Converters {
    companion object{
        @TypeConverter
        @JvmStatic
        fun fromTypeProperty(type: TypeProperty) = type.typeName

        @TypeConverter
        @JvmStatic
        fun toTypeProperty(type: String): TypeProperty {
            TypeProperty.values().forEach {
                if(type == it.typeName) return it
            }

            throw Exception("Type of Property not recognize")
        }

        @TypeConverter
        @JvmStatic
        fun fromTypeAmenity(type: TypeAmenity) = type.typeName

        @TypeConverter
        @JvmStatic
        fun toTypeAmenity(type: String): TypeAmenity {
            TypeAmenity.values().forEach {
                if(type == it.typeName) return it
            }

            throw Exception("Type of Amenity not recognize")
        }

        @TypeConverter
        @JvmStatic
        fun fromTimeStamp(value: Long?): Date? {
            value?.let{
                return Date(value)
            }
            return null

        }

        @TypeConverter
        @JvmStatic
        fun dateToTimeStamp(date: Date?): Long? {
            date?.let{
                return date.time
            }

            return null
        }
    }
}