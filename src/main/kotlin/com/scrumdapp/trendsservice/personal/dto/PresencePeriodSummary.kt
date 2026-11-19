package com.scrumdapp.trendsservice.personal.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class PresencePeriodSummary(
    @JsonProperty("from_date")
    val fromDate: LocalDate,

    @JsonProperty("to_date")
    val toDate: LocalDate,

    @JsonProperty("data_points")
    val dataPoints: List<Map<String, String>>
)