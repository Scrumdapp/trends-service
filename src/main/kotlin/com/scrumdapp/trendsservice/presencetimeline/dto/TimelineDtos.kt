package com.scrumdapp.trendsservice.presencetimeline.dto

import java.time.LocalDate

data class GroupPresenceTrends(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val trends: List<PresenceTrendItem>
)

data class PresenceTrendItem(
    val userId: Long,
    val days: List<PresenceTrendDay>
)

data class PresenceTrendDay(
    val date: LocalDate,
    val presences: List<PresenceTrendDayItem>
)

data class PresenceTrendDayItem(
    val presence: Number?,
    val name: String,
    val sessionId: Long
)