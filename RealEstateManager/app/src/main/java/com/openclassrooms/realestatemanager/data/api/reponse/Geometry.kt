package com.openclassrooms.realestatemanager.data.api.reponse


import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("location")
    val location: Location
)