package com.openclassrooms.realestatemanager.data.api.reponse


import com.google.gson.annotations.SerializedName

data class ProvidedLocation(
    @SerializedName("location")
    val location: String
)