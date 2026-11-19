package com.scrumdapp.trendsservice.personal

import com.scrumdapp.trendsservice.errors.BadRequestException
import com.scrumdapp.trendsservice.personal.dto.MonthlyPresenceSummary
import com.scrumdapp.trendsservice.personal.dto.PersonalTrendsSummary
import com.scrumdapp.trendsservice.personal.dto.PresencePeriodSummary
import com.scrumdapp.trendsservice.personal.dto.PresenceSummary
import com.scrumdapp.trendsservice.personal.dto.StarsSummary
import com.scrumdapp.trendsservice.personal.dto.StarsTrendSummary
import com.scrumdapp.trendsservice.presencetimeline.PresenceTimelineRepository
import com.scrumdapp.trendsservice.presencetimeline.dto.Presence
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

@Service
class PersonalTrendsService(
    private val timelineRepository: PresenceTimelineRepository,
) {

    fun getSummary(groupId: Long, userId: Long): PersonalTrendsSummary {
        val today = LocalDate.now()

        val currentWeekStart = today.with(
            TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        )
        val lastWeekStart = currentWeekStart.minusWeeks(1)
        val lastWeekEnd = currentWeekStart.minusDays(1)

        val thisMonthStart = today.withDayOfMonth(1)
        val lastMonthStart = thisMonthStart.minusMonths(1)
        val lastMonthEnd = thisMonthStart.minusDays(1)

        val from = minOf(lastWeekStart, lastMonthStart)

        val timelines = timelineRepository.getTimelinePresences(
            groupId,
            from,
            today
        )

        val currentWeek = PresenceCounter()
        val lastWeek = PresenceCounter()
        val thisMonth = PresenceCounter()
        val lastMonth = PresenceCounter()

        for (timeline in timelines) {
            val date = timeline.date
            val presence = timeline.presence

            if (date in currentWeekStart..today) {
                currentWeek.add(presence)
            }

            if (date in lastWeekStart..lastWeekEnd) {
                lastWeek.add(presence)
            }

            if (date in thisMonthStart..today) {
                thisMonth.add(presence)
            }

            if (date in lastMonthStart..lastMonthEnd) {
                lastMonth.add(presence)
            }
        }

        val stars = timelines
            .filter { it.userId == userId }
            .mapNotNull { it.stars }

        val starsSummary = StarsSummary(
            average = if (stars.isEmpty()) 0.0 else stars.average(),
            count = stars.size
        )

        return PersonalTrendsSummary(
            presence = PresenceSummary(
                currentWeek = currentWeek.toDto(),
                lastWeek = lastWeek.toDto(),
                thisMonth = thisMonth.toDto(),
                lastMonth = lastMonth.toDto()
            ),
            stars = starsSummary
        )
    }

    fun getMood(groupId: Long, userId: Long, fromDate: LocalDate, toDate: LocalDate): StarsTrendSummary {
        if(fromDate.isAfter(toDate)) {
            throw BadRequestException(message = "fromDate cannot be after toDate")
        }

        val timelines = timelineRepository.getTimelinePresences(
            groupId,
            fromDate,
            toDate
        )

        val userTimelines = timelines
            .filter { it.userId == userId && it.stars != null }
            .sortedBy { it.date }

        val scores = userTimelines.mapNotNull { it.stars }

        val points = userTimelines.map { timeline ->
            mapOf(
                "date" to timeline.date.toString(),
                "session_name" to (timeline.sessionName ?: "No name"),
                "score" to timeline.stars!!.toString()
            )
        }

        return StarsTrendSummary(
            fromDate = fromDate,
            toDate = toDate,
            average = if (scores.isEmpty()) 0.0 else scores.average(),
            count = scores.size,
            points = points
        )
    }

    fun getMonthlyPresence(userId: Long, groupId: Long, month: YearMonth): MonthlyPresenceSummary {
        val from = month.atDay(1)
        val to = month.atEndOfMonth()

        val timelines = timelineRepository.getTimelinePresences(
            groupId,
            from,
            to
        )

        val days = timelines
            .groupBy { it.date }
            .toSortedMap()
            .map { (date, dailyTimelines) ->

                val sessions = dailyTimelines
                    .distinctBy { it.sessionId.toString() }

                val details = sessions.map { session ->
                    val userTimeline = dailyTimelines.firstOrNull {
                        it.sessionId == session.sessionId &&
                                it.userId == userId
                    }

                    val status = when (
                        Presence.fromCode(userTimeline?.presence ?: -1)?.name
                    ) {
                        "ON_TIME" -> "present"
                        "VERIFIED_LATE" -> "late"
                        "SICK" -> "sick"
                        "ABSENT" -> "absent"
                        else -> "absent"
                    }

                    mapOf(
                        "name" to (session.sessionName ?: "No name"),
                        "status" to status
                    )
                }

                mapOf(
                    "date" to date.toString(),
                    "details" to details.toString(),
                )
            }

        return MonthlyPresenceSummary(
            month = month.toString(),
            days = days
        )
    }

    fun getPresencePeriod(userId: Long, groupId: Long, fromDate: LocalDate, toDate: LocalDate): PresencePeriodSummary {
        if(fromDate.isAfter(toDate)) {
            throw BadRequestException(message = "fromDate cannot be after toDate")
        }

        val timelines = timelineRepository.getTimelinePresences(
            groupId,
            fromDate,
            toDate
        )

        val sessions = timelines.groupBy { it.date to it.sessionId }

        val dataPoints = mutableListOf<Map<String, String>>()
        var periodStart = fromDate

        while (!periodStart.isAfter(toDate)) {
            val periodEnd = minOf(periodStart.plusDays(13), toDate)

            var present = 0
            var sick = 0
            var late = 0
            var absent = 0
            var noData = 0

            sessions
                .values
                .forEach { sessionTimelines ->
                    val userTimeline = sessionTimelines.firstOrNull {
                        it.userId == userId
                    }

                    when (Presence.fromCode(userTimeline?.presence ?: -1)?.name) {
                        "ON_TIME" -> present++
                        "VERIFIED_LATE" -> late++
                        "SICK" -> sick++
                        "ABSENT" -> absent++
                        else -> noData++
                    }
                }

            dataPoints.add(
                mapOf(
                    "from_date" to periodStart.toString(),
                    "to_date" to periodEnd.toString(),
                    "present" to present.toString(),
                    "sick" to sick.toString(),
                    "late" to late.toString(),
                    "absent" to absent.toString(),
                    "no_data" to noData.toString()
                )
            )

            periodStart = periodEnd.plusDays(1)
        }

        return PresencePeriodSummary(
            fromDate = fromDate,
            toDate = toDate,
            dataPoints = dataPoints
        )
    }
}