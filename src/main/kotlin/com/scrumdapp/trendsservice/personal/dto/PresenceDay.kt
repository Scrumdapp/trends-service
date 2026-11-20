package com.scrumdapp.trendsservice.personal.dto

data class PresenceDay(
    val date: String,
    val details: List<PresenceDetail>
)