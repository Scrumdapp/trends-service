package com.scrumdapp.trendsservice.personal.dto

data class PersonalTrendsSummary(
    val presence: PresenceSummary,
    val stars: StarsSummary
)