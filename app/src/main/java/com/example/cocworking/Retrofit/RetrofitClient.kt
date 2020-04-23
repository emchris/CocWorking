package com.example.cocworking.Retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private var instance: Retrofit?=null //dichiaro un'istanza di tipo Retrofit (?)
    private const val BASE_URL = "http://10.0.2.2:3000/"

    fun getInstance(): Retrofit {
        if(instance ==null)
            instance = Retrofit.Builder() //così creo un Retrofit object
                .baseUrl(BASE_URL) //Localhost will be changed in 10.0.2.2 in Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return instance!!
    }
}