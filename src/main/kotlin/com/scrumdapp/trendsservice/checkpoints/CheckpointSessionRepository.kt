package com.scrumdapp.trendsservice.checkpoints

import org.springframework.data.jpa.repository.JpaRepository

interface CheckpointSessionRepository : JpaRepository<CheckpointSession, Long> {
}