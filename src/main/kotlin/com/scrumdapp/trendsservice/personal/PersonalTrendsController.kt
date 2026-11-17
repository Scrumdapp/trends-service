package com.scrumdapp.trendsservice.personal

import com.scrumdapp.passportplugin.annotations.Passport
import com.scrumdapp.passportplugin.jwt.PassportContent
import com.scrumdapp.trendsservice.errors.ForbiddenException
import com.scrumdapp.trendsservice.errors.ServerFaultException
import com.scrumdapp.trendsservice.personal.dto.MonthlyPresenceSummary
import com.scrumdapp.trendsservice.personal.dto.PersonalTrendsSummary
import com.scrumdapp.trendsservice.personal.dto.PresencePeriodSummary
import com.scrumdapp.trendsservice.personal.dto.StarsTrendSummary
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.YearMonth

@RestController
@RequestMapping("/groups")
class PersonalTrendsController (
    val personalTrendsService: PersonalTrendsService
) {

    @GetMapping("/{groupId}/trends/{userId}/summary")
    fun getPersonalTrendsSummary(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @Passport passport: PassportContent,
    ): PersonalTrendsSummary {
        SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw ServerFaultException(message = "Auth principal couldn't be found or isn't a valid jwt. To prevent the endpoint is protected.")

        if (passport.userGroups?.contains(groupId.toInt()) == false) throw ForbiddenException(message = "You don't have access to this group")

        return personalTrendsService.getSummary(groupId, userId)
    }

    @GetMapping("/{groupId}/trends/{userId}/mood")
    fun getPersonalTrendsMood(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @RequestParam("fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate,
        @RequestParam("toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate,
        @Passport passport: PassportContent,
    ): StarsTrendSummary {
        SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw ServerFaultException(message = "Auth principal couldn't be found or isn't a valid jwt. To prevent the endpoint is protected.")

        if (passport.userGroups?.contains(groupId.toInt()) == false) throw ForbiddenException(message = "You don't have access to this group")

        return personalTrendsService.getMood(groupId, userId, fromDate, toDate)
    }

    @GetMapping("/{groupId}/trends/{userId}/calendar")
    fun getMonthlyPresence(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @RequestParam month: YearMonth,
        @Passport passport: PassportContent,
    ): MonthlyPresenceSummary {
        SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw ServerFaultException(message = "Auth principal couldn't be found or isn't a valid jwt. To prevent the endpoint is protected.")

        if (passport.userGroups?.contains(groupId.toInt()) == false) throw ForbiddenException(message = "You don't have access to this group")

        return personalTrendsService.getMonthlyPresence(userId, groupId, month)
    }

    @GetMapping("/{groupId}/trends/{userId}/period")
    fun getPresencePeriod(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @RequestParam("fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate,
        @RequestParam("toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate,
        @Passport passport: PassportContent,
    ): PresencePeriodSummary {
        SecurityContextHolder.getContext().authentication?.principal as? Jwt
            ?: throw ServerFaultException(message = "Auth principal couldn't be found or isn't a valid jwt. To prevent the endpoint is protected.")

        if (passport.userGroups?.contains(groupId.toInt()) == false) throw ForbiddenException(message = "You don't have access to this group")

        return personalTrendsService.getPresencePeriod(userId, groupId, fromDate, toDate)
    }
}