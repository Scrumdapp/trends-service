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

    override fun getGroupUsers(authorization: Jwt, groupId: Long): List<GroupUserResponse> {
        return listOf(
            GroupUserResponse(1, 1, "John", "Doe", false),
            GroupUserResponse(2, 1, "Teacher", "Peacher", true),
            GroupUserResponse(3, 1, "Gary", "Goodspeed", false),
        )
    }
}