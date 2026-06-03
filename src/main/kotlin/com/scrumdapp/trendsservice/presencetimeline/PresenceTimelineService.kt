package com.scrumdapp.trendsservice.presencetimeline

import com.scrumdapp.trendsservice.errors.BadRequestException
import com.scrumdapp.trendsservice.groups.GroupService
import com.scrumdapp.trendsservice.presencetimeline.dto.GroupPresenceTrends
import com.scrumdapp.trendsservice.presencetimeline.dto.PresenceTrendDay
import com.scrumdapp.trendsservice.presencetimeline.dto.PresenceTrendDayItem
import com.scrumdapp.trendsservice.presencetimeline.dto.PresenceTrendItem
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class PresenceTimelineService(
    private val timelineRepository: PresenceTimelineRepository,
    private val groupService: GroupService
) {

    fun getGroupTimeline(authorization: Jwt, groupId: Long, from: LocalDate, to: LocalDate): GroupPresenceTrends {
        val userIds = groupService.getGroupUserIds(authorization, groupId)

        // Highly presorted in database, assumptions can be made
        val timelines = timelineRepository.getTimelinePresences(groupId, from, to)

        if (timelines.isEmpty()) {
            throw BadRequestException(message = "No checkpoints found for date range")
        }

        val trendsMap = mutableMapOf<Long, PresenceTrendItem>()

        for (userId in userIds) {
            val item = PresenceTrendItem(userId, days = mutableListOf())
            trendsMap[userId] = item
        }

        var i = 0
        while (i < timelines.size) {
            val processingDate = timelines[i].date
            val processingTime = timelines[i].startedTime
            val name = timelines[i].sessionName
            val sessionId = timelines[i].sessionId
            val usersLeft = userIds.toHashSet()

            while (i < timelines.size) {
                val timeline = timelines[i]
                if (timeline.date != processingDate || timeline.startedTime != processingTime) {
                    break
                }
                i++
                if (!usersLeft.contains(timeline.userId)) { continue }
                usersLeft.remove(timeline.userId)
                val trendItem = trendsMap[timeline.userId] ?: continue

                EnsureLastDayPresent(trendItem.days as MutableList<PresenceTrendDay>, timeline.date)
                val day = trendItem.days.last()
                val presences = day.presences as MutableList
                presences.add(PresenceTrendDayItem(
                    timeline.presence,
                    name ?: "No name",
                    sessionId
                ))
            }

            for (userId in usersLeft) {
                val trendItem = trendsMap[userId] ?: continue

                EnsureLastDayPresent(trendItem.days as MutableList<PresenceTrendDay>, processingDate)
                val day = trendItem.days.last()
                val presences = day.presences as MutableList
                presences.add(PresenceTrendDayItem(
                    null,
                    name ?: "No name",
                    sessionId
                ))
            }
        }

        val trends = GroupPresenceTrends(
            timelines.first().date,
            timelines.last().date,
            trendsMap.values.toList()
        )

        return trends
    }

    private fun EnsureLastDayPresent(days: MutableList<PresenceTrendDay>, date: LocalDate) {
        if (!days.isEmpty()) {
            val lastDay = days.last()
            if (lastDay.date >= date) return
        }
        days.add(PresenceTrendDay(date, mutableListOf()))
    }

    private data class ProcessedDate(
        val date: LocalDate,
        val names: List<String>
    )
}