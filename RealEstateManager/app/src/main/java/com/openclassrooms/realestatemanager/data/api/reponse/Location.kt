package com.openclassrooms.realestatemanager.data.api.reponse


import com.google.gson.annotations.SerializedName

data class Location(
        @SerializedName("latLng")
    val latLng: LatLng,
        @SerializedName("mapUrl")
    val mapUrl: String,
        @SerializedName("street")
    val street: String,
        @SerializedName("adminArea1")
    val country: String,
        @SerializedName("adminArea3")
    val state: String,
        @SerializedName("adminArea5")
    val city: String,
        @SerializedName("adminArea6")
    val neighborhood: String,
        @SerializedName("postalCode")
    val postalCode: String

)