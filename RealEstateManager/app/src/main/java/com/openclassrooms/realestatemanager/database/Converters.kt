package com.openclassrooms.realestatemanager.database

import androidx.room.TypeConverter
import com.openclassrooms.realestatemanager.TypeAmenity
import com.openclassrooms.realestatemanager.TypeProperty

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
        fun toTypeProperty(type: String): TypeProperty{
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
        fun toTypeAmenity(type: String): TypeAmenity{
            TypeAmenity.values().forEach {
                if(type == it.typeName) return it
            }

            throw Exception("Type of Amenity not recognize")
        }
    }
}