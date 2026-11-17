package com.scrumdapp.trendsservice.personal

import com.scrumdapp.trendsservice.presencetimeline.dto.Presence
import com.scrumdapp.trendsservice.personal.dto.PresenceStats

internal class PresenceCounter {
    private var onTime = 0
    private var delayed = 0
    private var noData = 0

    fun add(code: Int?) {
        when (Presence.fromCode(code ?: -1)?.name) {
            "ON_TIME" -> onTime++
            "VERIFIED_LATE" -> delayed++
            else -> noData++
        }
    }

    fun toDto() = PresenceStats(
        onTime = onTime,
        delayed = delayed,
        noData = noData
    )
}