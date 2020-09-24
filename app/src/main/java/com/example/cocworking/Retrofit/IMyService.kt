package com.example.cocworking.Retrofit

import com.example.cocworking.models.Event
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.time.LocalDateTime

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

    @POST("updateEvents")
    @FormUrlEncoded
    fun updateEvents(@Field("eventId") eventId: String,
                     @Field("userId") userId: String,
                     @Field("text") text: String,
                     @Field("date") date: LocalDateTime) : Call<String>

    @POST("takeEvents")
    @FormUrlEncoded
    fun takeEvents(@Field("userId") userId: String) : Call<List<EventsUpdated>>

}