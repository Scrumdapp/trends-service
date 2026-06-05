package com.scrumdapp.trendsservice.security

import com.scrumdapp.passportplugin.filters.PassportAuthFilter
import com.scrumdapp.passportplugin.filters.usePassport
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
    private val passportAuthFilter: PassportAuthFilter
) {

    @Bean
    fun httpSecurityChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .usePassport(passportAuthFilter)
            .authorizeHttpRequests { it
                .requestMatchers(HttpMethod.GET, "/trends/grouptimeline/{groupId}").hasAnyAuthority("STUDENT", "COACH")
            }

        return http.build()
    }
}