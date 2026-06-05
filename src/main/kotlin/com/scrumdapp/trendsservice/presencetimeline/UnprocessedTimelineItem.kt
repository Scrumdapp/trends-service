package com.scrumdapp.trendsservice.presencetimeline

import java.time.LocalDate
import java.time.LocalTime

data class UnprocessedTimelineItem(
    val userId: Long,
    val date: LocalDate,
    val startedTime: LocalTime,
    val sessionId: Long,
    val sessionName: String?,
    val presence: Int?
)