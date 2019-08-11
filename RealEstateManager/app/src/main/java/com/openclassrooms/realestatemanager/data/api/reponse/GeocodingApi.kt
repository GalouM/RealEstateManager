package com.openclassrooms.realestatemanager.data.api.reponse


import com.google.gson.annotations.SerializedName

data class GeocodingApi(
        @SerializedName("results")
    val results: List<Result>
)