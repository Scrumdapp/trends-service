package com.scrumdapp.trendsservice.presencetimeline

import com.scrumdapp.trendsservice.checkpoints.Checkpoint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface PresenceTimelineRepository : JpaRepository<Checkpoint, Long> {
    @Query("""
        SELECT
            new com.scrumdapp.trendsservice.presencetimeline.UnprocessedTimelineItem(
                cp.groupUserId,
                cp.checkpointSession.createdDate,
                cp.checkpointSession.startTime,
                cp.checkpointSession.id,
                cp.checkpointSession.name,
                cp.presence
            )
        FROM Checkpoint cp
            WHERE cp.checkpointSession.groupId = :groupId
                AND cp.checkpointSession.createdDate >= :from
                AND cp.checkpointSession.createdDate <= :to
            ORDER BY cp.checkpointSession.createdDate, cp.checkpointSession.startTime
    """)
    fun getTimelinePresences(
        @Param("groupId") groupId: Long,
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate
    ): List<UnprocessedTimelineItem>;
}