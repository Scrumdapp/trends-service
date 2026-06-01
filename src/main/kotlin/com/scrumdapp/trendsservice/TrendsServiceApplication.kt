package com.scrumdapp.trendsservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TrendsServiceApplication

fun main(args: Array<String>) {
    runApplication<TrendsServiceApplication>(*args)
}
