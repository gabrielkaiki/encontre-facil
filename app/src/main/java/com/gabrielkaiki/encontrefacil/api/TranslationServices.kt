package com.gabrielkaiki.encontrefacil.api

import com.gabrielkaiki.encontrefacil.models.NearbySearch
import com.gabrielkaiki.encontrefacil.models.Translation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslationServices {

    @GET("v2")
    fun getTranslation(
        @Query("q") q: String,
        @Query("source") source: String,
        @Query("target") target: String,
        @Query("format") format: String,
        @Query("key") key: String
    ): Call<Translation>
}