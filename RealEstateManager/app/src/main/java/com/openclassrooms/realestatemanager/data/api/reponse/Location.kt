package com.openclassrooms.realestatemanager.data.api.reponse


import com.google.gson.annotations.SerializedName

data class Location(
        @SerializedName("latLng")
    val latLng: LatLng,
        @SerializedName("mapUrl")
    val mapUrl: String,
        @SerializedName("adminArea5")
    val neighborhood: String

)