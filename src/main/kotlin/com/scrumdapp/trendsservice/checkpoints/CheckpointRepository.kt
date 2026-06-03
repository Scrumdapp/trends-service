package com.scrumdapp.trendsservice.checkpoints

import org.springframework.data.jpa.repository.JpaRepository

interface CheckpointRepository : JpaRepository<Checkpoint, Long> {

}