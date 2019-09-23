package com.openclassrooms.realestatemanager.provider

import android.content.ContentValues
import com.openclassrooms.realestatemanager.data.database.Converters
import com.openclassrooms.realestatemanager.data.entity.*
import com.openclassrooms.realestatemanager.utils.TypeAmenity
import java.util.*

/**
 * Created by galou on 2019-09-20
 */

fun propertyFromContentValues(values: ContentValues): Property{
    val property = Property()
    if(values.containsKey("property_id")) property.id = values.getAsString("property_id")
    if(values.containsKey("type_property")) property.type = Converters.toTypeProperty(values.getAsString("type_property"))
    if(values.containsKey("price")) property.price = values.getAsDouble("price")
    if(values.containsKey("surface")) property.surface = values.getAsDouble("surface")
    if(values.containsKey("rooms")) property.rooms = values.getAsInteger("rooms")
    if(values.containsKey("bedrooms")) property.bedrooms = values.getAsInteger("bedrooms")
    if(values.containsKey("bathrooms")) property.bathrooms = values.getAsInteger("bathrooms")
    if(values.containsKey("description")) property.id = values.getAsString("description")
    if(values.containsKey("on_market_since")) property.onMarketSince = Date(values.getAsLong("on_market_since"))
    if(values.containsKey("sold")) property.sold = values.getAsBoolean("sold")
    if(values.containsKey("sell_date")) property.onMarketSince = Date(values.getAsLong("sell_date"))
    if(values.containsKey("agent")) property.agent = values.getAsString("agent")
    if(values.containsKey("has_picture")) property.hasPictures = values.getAsBoolean("has_picture")
    if(values.containsKey("creation_date")) property.creationDate = Date(values.getAsLong("creation_date"))
    return property
}

fun pictureFromContentValues(values: ContentValues): List<Picture> {
    val pictures = mutableListOf<Picture>()
    var pictureIds= listOf<String>()
    var urls= listOf<String>()
    var thumbnailUrls = listOf<String>()
    var serverUrls = listOf<String>()
    var idProperty= listOf<String>()
    var descriptions= listOf<String>()
    var orderNumbers = listOf<String>()

    if (values.containsKey("picture_id")) pictureIds = values.getAsString("picture_id").split("&")
    if (values.containsKey("url")) urls = values.getAsString("url").split("&")
    if (values.containsKey("thumbnail_url")) thumbnailUrls = values.getAsString("thumbnail_url").split("&")
    if(values.containsKey("server_url")) serverUrls = values.getAsString("server_url").split("&")
    if(values.containsKey("id_property")) idProperty = values.getAsString("id_property").split("&")
    if(values.containsKey("description")) descriptions = values.getAsString("description").split("&")
    if(values.containsKey("order_number")) orderNumbers = values.getAsString("order_number").split("&")

    if(pictureIds.size == urls.size && pictureIds.size == thumbnailUrls.size && pictureIds.size == serverUrls.size
            && pictureIds.size == idProperty.size && pictureIds.size == descriptions.size && pictureIds.size == orderNumbers.size){
        pictureIds.forEachIndexed { index, id ->
            pictures.add(Picture(
                    id = id, url = urls[index], thumbnailUrl = thumbnailUrls[index],
                    serverUrl = serverUrls[index], property = idProperty[index], description = descriptions[index],
                    orderNumber = orderNumbers[index].toIntOrNull()
                    ))
        }
    }
    return pictures
}

fun amenityFromContentValues(values: ContentValues): List<Amenity> {
    val amenities = mutableListOf<Amenity>()
    var amenitiesId = listOf<String>()
    var propertyId = listOf<String>()
    val type = mutableListOf<TypeAmenity>()
    var typeString = listOf<String>()

    if (values.containsKey("amenity_id")) amenitiesId = values.getAsString("amenity_id").split("&")
    if (values.containsKey("property")) propertyId = values.getAsString("property").split("&")
    if (values.containsKey("type_amenity")) typeString = values.getAsString("type_amenity").split("&")
    typeString.forEach { typeAmenity ->
        type.add(Converters.toTypeAmenity(typeAmenity))

    }
    if(amenitiesId.size == propertyId.size && amenitiesId.size == type.size){
        amenitiesId.forEachIndexed { index, id ->
            amenities.add(Amenity(id = id, property = propertyId[index], type = type[index]))
        }
    }
    return amenities
}

fun agentFromContentValues(values: ContentValues): Agent{
    val agent = Agent()
    if (values.containsKey("agent_id")) agent.id = values.getAsString("agent_id")
    if (values.containsKey("first_name")) agent.firstName = values.getAsString("first_name")
    if (values.containsKey("last_name")) agent.lastName = values.getAsString("last_name")
    if (values.containsKey("email")) agent.email = values.getAsString("email")
    if (values.containsKey("phone_number")) agent.phoneNumber = values.getAsString("phone_number")
    if (values.containsKey("url_picture")) agent.urlProfilePicture = values.getAsString("url_picture")
    if (values.containsKey("creation_date")) agent.creationDate = Date(values.getAsLong("creation_date"))
    return agent
}

fun addressFromContentValues(values: ContentValues): Address {
    val address = Address()
    if (values.containsKey("address_id")) address.propertyId = values.getAsString("address_id")
    if (values.containsKey("street")) address.street = values.getAsString("street")
    if (values.containsKey("city")) address.city = values.getAsString("city")
    if (values.containsKey("country")) address.country = values.getAsString("country")
    if (values.containsKey("postalCode")) address.postalCode = values.getAsString("postalCode")
    if (values.containsKey("state")) address.state = values.getAsString("state")
    if (values.containsKey("longitude")) address.longitude = values.getAsDouble("longitude")
    if (values.containsKey("latitude")) address.latitude = values.getAsDouble("latitude")
    if (values.containsKey("neighbourhood")) address.neighbourhood = values.getAsString("neighbourhood")
    if (values.containsKey("map_icon_url")) address.mapIconUrl = values.getAsString("map_icon_url")
    if (values.containsKey("address_for_display")) address.addressForDisplay = values.getAsString("address_for_display")
    return address
}