package com.scrumdapp.trendsservice.errors

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiExceptionResponse(
    val code: Int,
    val message: String?,
    val stackTrace: String? = null
)
