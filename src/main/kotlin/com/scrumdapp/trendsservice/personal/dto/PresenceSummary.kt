package com.scrumdapp.trendsservice.personal.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class PresenceSummary(
    @JsonProperty("current_week")
    val currentWeek: PresenceStats,

    @JsonProperty("last_week")
    val lastWeek: PresenceStats,

    @JsonProperty("this_month")
    val thisMonth: PresenceStats,

    @JsonProperty("last_month")
    val lastMonth: PresenceStats
)