package com.scrumdapp.trendsservice.datascraper

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient.builder
import org.springframework.web.client.toEntity
import tools.jackson.module.kotlin.jacksonObjectMapper

@Component
class ServiceRequester {

    private val mapper = jacksonObjectMapper()

    private fun <T> performRequest(
        baseUrl: String,
        uri: String,
        type: Class<T>,
    ): T {

        val reqBuilder = builder().baseUrl(baseUrl).build()

        val reqSpec = reqBuilder
            .method(HttpMethod.GET)
            .uri(uri)
            .header("Authorization", "Bearer ")
            .accept(MediaType.APPLICATION_JSON)

        try {
            val res = reqSpec.retrieve().toEntity<String>()

            if (res.statusCode == HttpStatus.OK) {
                return mapper.readValue(res.body, type)
            } else {
                throw Exception("Request failed with status ${res.statusCode}")
            }
        } catch (e: HttpClientErrorException) {
            throw Exception("Request failed with status ${e.statusCode}")
        }
    }
}