package com.example.cocworking.Retrofit
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient2 {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    val instance: IMyService by lazy {
        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()

        retrofit.create(IMyService::class.java)
    }

}