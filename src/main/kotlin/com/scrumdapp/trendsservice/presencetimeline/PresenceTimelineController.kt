package com.scrumdapp.trendsservice.presencetimeline

import com.scrumdapp.trendsservice.errors.BadRequestException
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
        @RequestParam("from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate?,
        @RequestParam("to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate?
    ): GroupPresenceTrends {
        val jwt = SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw ServerFaultException(message = "Auth principal couldn't be found or isn't a valid jwt. To prevent the endpoint is protected.")

        if (fromDate == null) {
            throw BadRequestException(message="Missing query parameter 'from'")
        }

        if (toDate == null) {
            throw BadRequestException(message="Missing query parameter 'to'")
        }

        return timelineService.getGroupTimeline(jwt, groupId, fromDate, toDate)
    }
}