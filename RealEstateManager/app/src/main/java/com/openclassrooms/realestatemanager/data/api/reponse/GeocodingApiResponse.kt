package com.openclassrooms.realestatemanager.data.api.reponse


import com.google.gson.annotations.SerializedName

data class GeocodingApiResponse(
        @SerializedName("results")
    val results: List<Result>
)