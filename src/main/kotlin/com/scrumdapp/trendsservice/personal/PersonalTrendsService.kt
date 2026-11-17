package com.scrumdapp.trendsservice.personal

import com.scrumdapp.trendsservice.personal.dto.PersonalTrendsSummary
import com.scrumdapp.trendsservice.personal.dto.PresenceSummary
import com.scrumdapp.trendsservice.personal.dto.StarsSummary
import com.scrumdapp.trendsservice.presencetimeline.PresenceTimelineRepository
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@Service
class PersonalTrendsService(
    private val timelineRepository: PresenceTimelineRepository
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
}