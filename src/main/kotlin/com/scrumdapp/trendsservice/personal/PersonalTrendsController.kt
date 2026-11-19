package com.scrumdapp.trendsservice.personal

import com.scrumdapp.passportplugin.annotations.groupAccess.IsInGroup
import com.scrumdapp.passportplugin.annotations.passportController.Passport
import com.scrumdapp.passportplugin.jwt.PassportContent
import com.scrumdapp.trendsservice.errors.BadRequestException
import com.scrumdapp.trendsservice.errors.ForbiddenException
import com.scrumdapp.trendsservice.personal.dto.MonthlyPresenceSummary
import com.scrumdapp.trendsservice.personal.dto.PersonalTrendsSummary
import com.scrumdapp.trendsservice.personal.dto.PresencePeriodSummary
import com.scrumdapp.trendsservice.personal.dto.StarsTrendSummary
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.YearMonth

@RestController
@RequestMapping("/groups/{groupId}/trends/{userId}")
class PersonalTrendsController (
    val personalTrendsService: PersonalTrendsService
) {

    @IsInGroup
    @GetMapping("/summary")
    fun getPersonalTrendsSummary(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @Passport passport: PassportContent,
    ): PersonalTrendsSummary {
        if(passport.userId != userId && !passport.roles.contains("COACH")) {throw ForbiddenException(message = "You can only see your own personal trends")}

        return personalTrendsService.getSummary(groupId, userId)
    }

    @IsInGroup
    @GetMapping("/mood")
    fun getPersonalTrendsMood(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @RequestParam("fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate?,
        @RequestParam("toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate?,
        @Passport passport: PassportContent,
    ): StarsTrendSummary {
        if(passport.userId != userId && !passport.roles.contains("COACH")) {throw ForbiddenException(message = "You can only see your own personal trends")}

        if (fromDate == null) throw BadRequestException(message="Missing query parameter 'from'")

        if (toDate == null) throw BadRequestException(message="Missing query parameter 'to'")

        return personalTrendsService.getMood(groupId, userId, fromDate, toDate)
    }

    @IsInGroup
    @GetMapping("/calendar")
    fun getMonthlyPresence(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @RequestParam month: YearMonth,
        @Passport passport: PassportContent,
    ): MonthlyPresenceSummary {
        if(passport.userId != userId && !passport.roles.contains("COACH")) {throw ForbiddenException(message = "You can only see your own personal trends")}

        return personalTrendsService.getMonthlyPresence(userId, groupId, month)
    }

    @IsInGroup
    @GetMapping("/period")
    fun getPresencePeriod(
        @PathVariable groupId: Long,
        @PathVariable userId: Long,
        @RequestParam("fromDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate?,
        @RequestParam("toDate", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate?,
        @Passport passport: PassportContent,
    ): PresencePeriodSummary {
        if(passport.userId != userId && !passport.roles.contains("COACH")) {throw ForbiddenException(message = "You can only see your own personal trends")}

        if (fromDate == null) throw BadRequestException(message="Missing query parameter 'from'")

        if (toDate == null) throw BadRequestException(message="Missing query parameter 'to'")

        return personalTrendsService.getPresencePeriod(userId, groupId, fromDate, toDate)
    }
}