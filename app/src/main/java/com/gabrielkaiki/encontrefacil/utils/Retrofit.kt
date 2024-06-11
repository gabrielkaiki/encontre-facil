package com.gabrielkaiki.encontrefacil.utils

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun getRetrofitLocaisDeBusca(): Retrofit {
    var retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/maps/api/place/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit
}

fun getRetrofitTraducao(): Retrofit {
    var retrofit = Retrofit.Builder()
        .baseUrl("https://translation.googleapis.com/language/translate/")
        .addConverterFactory(GsonConverterFactory.create())
        .build();

    return retrofit
}