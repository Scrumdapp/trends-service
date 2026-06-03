package com.scrumdapp.trendsservice.presencetimeline

import com.scrumdapp.trendsservice.errors.ServerFaultException
import com.scrumdapp.trendsservice.presencetimeline.dto.GroupPresenceTrends
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/trends/grouptimeline")
class PresenceTimelineController (
    val timelineService: PresenceTimelineService
) {

    @GetMapping("/{groupId}")
    fun getGroupPresenceTimeline(
        @PathVariable groupId: Long,
        @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate,
        @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate
    ): GroupPresenceTrends {
        val jwt = SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw ServerFaultException(message = "Auth principal couldn't be found or isn't a valid jwt. To prevent the endpoint is protected.")

        return timelineService.getGroupTimeline(jwt, groupId, fromDate, toDate)
    }
}