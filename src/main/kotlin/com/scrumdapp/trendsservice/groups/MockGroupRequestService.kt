package com.scrumdapp.trendsservice.groups

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Primary
@ConditionalOnBooleanProperty(name=["USE_MOCK_SERVICE"], havingValue = true)
class MockGroupRequestService : GroupService {
    private val Logger = LoggerFactory.getLogger(MockGroupRequestService::class.java)

    init {
        Logger.warn("Using Mock Service")
    }

    override fun getGroupUserIds(authorization: Jwt, groupId: Long): List<Long> {
        return listOf(1, 2, 3)
    }
}