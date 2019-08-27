package com.openclassrooms.realestatemanager.data.api

import com.openclassrooms.realestatemanager.BuildConfig
import com.openclassrooms.realestatemanager.data.api.reponse.GeocodingApi
import com.openclassrooms.realestatemanager.utils.API_KEY_MAP_QUEST
import com.openclassrooms.realestatemanager.utils.BASE_URL_MAP_QUEST
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by galou on 2019-08-10
 */

interface GeocodingApiService{

    @GET("geocoding/v1/address?maxResults=1&key=$API_KEY_MAP_QUEST")
    fun getLocationFromAddress(@Query("location") location: String): Observable<GeocodingApi>

    companion object {

        fun create(): GeocodingApiService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL_MAP_QUEST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build()

            return retrofit.create(GeocodingApiService::class.java)
        }
    }



}