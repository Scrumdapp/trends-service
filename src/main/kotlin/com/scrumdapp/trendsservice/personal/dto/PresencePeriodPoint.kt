package com.scrumdapp.trendsservice.personal.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PresencePeriodPoint(
    @JsonProperty("from_date")
    val fromDate: String,

    @JsonProperty("to_date")
    val toDate: String,

    val present: Int,
    val sick: Int,
    val late: Int,
    val absent: Int,

    @JsonProperty("no_data")
    val noData: Int
)