package com.openclassrooms.realestatemanager.data.api.reponse


import com.google.gson.annotations.SerializedName

data class Result(
        @SerializedName("locations")
    val locations: List<Location>,
        @SerializedName("providedLocation")
    val providedLocation: ProvidedLocation
)