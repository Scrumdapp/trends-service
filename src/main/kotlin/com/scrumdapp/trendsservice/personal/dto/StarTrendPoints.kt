package com.scrumdapp.trendsservice.personal.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class StarsTrendPoint(
    val date: String,

    @JsonProperty("session_name")
    val sessionName: String,

    val score: String
)