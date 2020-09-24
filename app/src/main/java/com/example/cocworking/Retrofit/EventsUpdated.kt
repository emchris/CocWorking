package com.example.cocworking.Retrofit

import com.example.cocworking.models.Event
import java.time.LocalDateTime
import java.util.*

data class EventsUpdated(
    //val eventDictionary: () -> Unit = {}
    val _id: String,
    val eventId: String,
    val userId: String,
    val text: String,
    val date: String
)