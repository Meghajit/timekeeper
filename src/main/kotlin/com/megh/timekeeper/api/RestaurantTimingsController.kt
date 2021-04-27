package com.megh.timekeeper.api

import com.megh.timekeeper.domain.RestaurantTimingsValidator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestaurantTimingsController(private val restaurantTimingsValidator: RestaurantTimingsValidator) {

    @PostMapping("/v1/timings/format")
    fun postTimings(@RequestBody restaurantTimings: RestaurantTimings): ResponseEntity<HttpStatus> {
        restaurantTimingsValidator.validate(restaurantTimings)
        return ResponseEntity(HttpStatus.OK)
    }
}