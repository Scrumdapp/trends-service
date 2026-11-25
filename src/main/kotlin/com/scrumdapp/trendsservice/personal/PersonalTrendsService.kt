package com.scrumdapp.trendsservice.personal

import com.scrumdapp.trendsservice.errors.BadRequestException
import com.scrumdapp.trendsservice.personal.dto.MonthlyPresenceSummary
import com.scrumdapp.trendsservice.personal.dto.PersonalTrendsSummary
import com.scrumdapp.trendsservice.personal.dto.PresenceDay
import com.scrumdapp.trendsservice.personal.dto.PresenceDetail
import com.scrumdapp.trendsservice.personal.dto.PresencePeriodPoint
import com.scrumdapp.trendsservice.personal.dto.PresencePeriodSummary
import com.scrumdapp.trendsservice.personal.dto.PresenceSummary
import com.scrumdapp.trendsservice.personal.dto.StarsSummary
import com.scrumdapp.trendsservice.personal.dto.StarsTrendPoint
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
            if (timeline.userId != userId) continue

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
            StarsTrendPoint(
                date = timeline.date.toString(),
                sessionName = timeline.sessionName ?: "No name",
                score = timeline.stars.toString()
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
                    .distinctBy { it.sessionId }

                val details = sessions.map { session ->
                    val userTimeline = dailyTimelines.firstOrNull {
                        it.sessionId == session.sessionId &&
                                it.userId == userId
                    }

                    val status = when (
                        Presence.fromCode(userTimeline?.presence ?: -1)
                    ) {
                        Presence.ON_TIME, Presence.ONLINE -> "present"
                        Presence.LATE, Presence.VERIFIED_LATE -> "late"
                        Presence.SICK -> "sick"
                        else -> "absent"
                    }

                    PresenceDetail(
                        name = session.sessionName ?: "No name",
                        status = status
                    )
                }

                PresenceDay(
                    date = date.toString(),
                    details = details
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

        val dataPoints = mutableListOf<PresencePeriodPoint>()
        var periodStart = fromDate

        while (!periodStart.isAfter(toDate)) {
            val periodEnd = minOf(periodStart.plusDays(13), toDate)

            var present = 0
            var sick = 0
            var late = 0
            var absent = 0
            var noData = 0

            sessions.entries
                .filter { (key, _) -> key.first in periodStart..periodEnd }
                .map { it.value }
                .forEach { sessionTimelines ->
                    val userTimeline = sessionTimelines.firstOrNull {
                        it.userId == userId
                    }

                    when (Presence.fromCode(userTimeline?.presence ?: -1)) {
                        Presence.ON_TIME, Presence.ONLINE -> present++
                        Presence.LATE, Presence.VERIFIED_LATE -> late++
                        Presence.SICK -> sick++
                        Presence.ABSENT, Presence.VERIFIED_ABSENT -> absent++
                        else -> noData++
                    }
                }

            dataPoints.add(
                PresencePeriodPoint(
                    fromDate = periodStart.toString(),
                    toDate = periodEnd.toString(),
                    present = present,
                    sick = sick,
                    late = late,
                    absent = absent,
                    noData = noData
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