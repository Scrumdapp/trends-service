package com.scrumdapp.trendsservice.personal.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PresenceStats(
    @JsonProperty("on_time")
    val onTime: Int,

    val delayed: Int,

    @JsonProperty("no_data")
    val noData: Int
)