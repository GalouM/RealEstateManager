package com.openclassrooms.realestatemanager.contentProviderTest

import android.content.ContentValues
import com.openclassrooms.realestatemanager.data.database.Converters
import com.openclassrooms.realestatemanager.data.entity.Address
import com.openclassrooms.realestatemanager.data.entity.Amenity
import com.openclassrooms.realestatemanager.data.entity.Picture
import com.openclassrooms.realestatemanager.data.entity.Property
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import com.openclassrooms.realestatemanager.utils.TypeProperty
import com.openclassrooms.realestatemanager.utils.idGenerated
import com.openclassrooms.realestatemanager.utils.todaysDate
import java.util.*

/**
 * Created by galou on 2019-09-24
 */

fun generateAgentContentValue(id: String): ContentValues{
    val value = ContentValues()
    value.put("agent_id", id)
    value.put("first_name", "Gaelle")
    value.put("last_name", "Minisini")
    value.put("email", "galou@rem.com")
    value.put("phone_number", "6666666666")
    value.put("url_picture", "myUrl")
    value.put("creation_date", todaysDate.time)

    return value
}

fun generatePropertyAndDataContentValue(idProperty: String, idAgent: String): ContentValues{
    val value = ContentValues()
    value.put("property_id", idProperty)
    value.put("type_property", TypeProperty.PENTHOUSE.typeName)
    value.put("price", 100000.0)
    value.put("surface", 120.0)
    value.put("rooms", 3)
    value.put("bedrooms", 1)
    value.put("bathrooms", 1)
    value.put("description", "nice house")
    value.put("on_market_since", todaysDate.time)
    value.put("sold", false)
    value.put("agent", idAgent)
    value.put("has_picture", true)
    value.put("creation_date", todaysDate.time)

    value.put("picture_id", idGenerated)
    value.put("url", "myUrl")
    value.put("thumbnail_url", "url")
    value.put("server_url", "url server")
    value.put("id_property", idProperty)
    value.put("description", "description")
    value.put("order_number", 1)

    value.put("amenity_id", idGenerated)
    value.put("property", idProperty)
    value.put("type_amenity", TypeAmenity.BUSES.typeName)

    value.put("address_id", idProperty)
    value.put("street", "123 rd")
    value.put("city", "My City")
    value.put("country", "My country")
    value.put("postal_code", "34567")
    value.put("state", "state")
    value.put("longitude", 33.0)
    value.put("latitude", 44.0)
    value.put("neighbourhood", "my neighborhood")
    value.put("map_icon_url", "urlMap")
    value.put("address_for_display", "123 rd My City")

    return value

}
