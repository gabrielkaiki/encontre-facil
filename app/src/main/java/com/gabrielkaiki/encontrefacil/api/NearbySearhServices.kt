package com.gabrielkaiki.encontrefacil.api

import com.gabrielkaiki.encontrefacil.models.NearbySearch
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NearbySearhServices {

    @GET("nearbysearch/json")
    fun getLocaisProximos(
        @Query("location") location: String,
        @Query("radius") radius: String,
        @Query("type") type: String,
        @Query("language") language: String,
        @Query("key") key: String
    ): Call<NearbySearch>
}