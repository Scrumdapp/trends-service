package com.scrumdapp.trendsservice.groups

import org.springframework.security.oauth2.jwt.Jwt

interface GroupService {
    fun getGroupUserIds(authorization: Jwt, groupId: Long): List<Long>
}