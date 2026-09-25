package com.victorstudio.victorpremium.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val okHttp = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // baseUrl "dummy": todas las llamadas usan @Url absoluta (Dropbox),
    // Retrofit exige una base igual para poder construirse.
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://default.url/")
        .client(okHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
