package com.scrumdapp.trendsservice.errors

    import org.springframework.http.ResponseEntity
    import org.springframework.web.bind.MethodArgumentNotValidException
    import org.springframework.web.bind.annotation.ExceptionHandler
    import org.springframework.web.bind.annotation.RestControllerAdvice

    @RestControllerAdvice
    class ExceptionHandler {

        @ExceptionHandler(ApiException::class)
        fun handleApiException(e: ApiException): ResponseEntity<ApiExceptionResponse> {
            return ResponseEntity.status(e.code).body(ApiExceptionResponse(e.code, e.message))
        }

        @ExceptionHandler(MethodArgumentNotValidException::class)
        fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiExceptionResponse> {
            val message = e.bindingResult.fieldErrors
                .joinToString(", ") { it.defaultMessage ?: "Invalid field" }
            return ResponseEntity.status(400).body(ApiExceptionResponse(400, message))
        }
    }
