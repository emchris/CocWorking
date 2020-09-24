package com.example.cocworking.models

import java.time.LocalDate
import java.time.LocalDateTime

data class Event(val eventId: String, val userId: String?, val text: String, val date: LocalDateTime)

