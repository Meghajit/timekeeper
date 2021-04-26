package com.megh.timekeeper.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestaurantTimingsController {

    @PostMapping("/v1/timings/format")
    fun postTimings(@RequestBody restaurantTimings: RestaurantTimings):ResponseEntity<HttpStatus> {
        return ResponseEntity(HttpStatus.OK)
    }
}