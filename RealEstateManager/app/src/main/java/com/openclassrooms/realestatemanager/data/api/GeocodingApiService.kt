package com.openclassrooms.realestatemanager.data.api

import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApiResponse
import com.openclassrooms.realestatemanager.utils.BASE_URL_MAP_API
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by galou on 2019-08-10
 */

interface GeocodingApiService{

    @GET("geocode/json?")
    fun getLocationFromAddress(
            @Query("address") address: String,
            @Query("key") apiKey: String
    ): Observable<GeocodingApiResponse>

    companion object {

        fun create(): GeocodingApiService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL_MAP_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(GeocodingApiService::class.java)
        }
    }



}