package com.example.cocworking.Retrofit

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IMyService { //in questa interfaccia definisco tutte le api calls, faccio ritornare delle call
    @POST("register") //definisco l'url a cui questa funzione fa riferimento
    @FormUrlEncoded
    //dichiaro una funzione che richiamera la post relativa alla regsitrazione scritta nell'app express
    fun registerUser(@Field("email") email: String,
                     @Field("name") name: String,
                     @Field("password") password: String) : Call<String> //una Call è un oggetto e una chiamata potenzialmente eseguibile che "impacchetta" qualcosa, in questo caso una stringa

    @POST("login")
    @FormUrlEncoded
    fun loginUser(@Field("email") email: String,
                  @Field("password") password: String) : Call<DefaultResponse>
}