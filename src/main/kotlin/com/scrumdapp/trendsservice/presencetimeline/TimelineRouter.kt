package com.scrumdapp.trendsservice.presencetimeline

import com.scrumdapp.passportplugin.annotations.Passport
import com.scrumdapp.passportplugin.jwt.PassportContent
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/trends/grouptimeline")
class TimelineRouter {

    @GetMapping("/{groupId}")
    fun getGroupTimeline(
        @PathVariable groupId: Int,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate?,
        @Passport passport: PassportContent
    ) {
        if (passport.userGroups != null && !passport.userGroups!!.contains(groupId)) {
            throw Exception("Forbidden")
        }

        // Compare dates
        // Call service
        // Fetch sessions from cp service
        // Fetch presence from cp service
        // Aggregate information to DTO
        // Return
    }
}