package com.scrumdapp.trendsservice.presencetimeline

import java.time.LocalDate

data class GroupPresenceTrends(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val trends: List<PresenceTrendItem>
)

data class PresenceTrendItem(
    val userId: Int,
    val days: List<PresenceTrendDay>
)

data class PresenceTrendDay(
    val date: LocalDate,
    val presences: List<PresenceTrendDayItem>
)

data class PresenceTrendDayItem(
    val presence: String,
    val sessionId: Int
)