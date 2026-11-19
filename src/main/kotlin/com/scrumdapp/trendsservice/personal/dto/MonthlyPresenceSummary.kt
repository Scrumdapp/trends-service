package com.scrumdapp.trendsservice.personal.dto

data class MonthlyPresenceSummary(
    val month: String,
    val days: List<Map<String, String>>
)