package com.scrumdapp.trendsservice.personal.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class StarsTrendSummary(
    @JsonProperty("from_date")
    val fromDate: LocalDate,

    @JsonProperty("to_date")
    val toDate: LocalDate,

    val average: Double,
    val count: Int,
    val points: List<Map<String, String>>
)