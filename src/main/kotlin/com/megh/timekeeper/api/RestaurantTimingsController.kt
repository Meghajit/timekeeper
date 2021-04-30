package com.megh.timekeeper.api

import com.megh.timekeeper.domain.RestaurantTimingsValidator
import com.megh.timekeeper.service.FormattedRestaurantTimings
import com.megh.timekeeper.service.RestaurantTimingsFormatter
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class RestaurantTimingsController(
    private val restaurantTimingsValidator: RestaurantTimingsValidator,
    private val restaurantTimingsFormatter: RestaurantTimingsFormatter
) {

    @PostMapping("/v1/timings/format", produces = [APPLICATION_JSON_VALUE])
    fun postTimings(@RequestBody restaurantTimings: RestaurantTimings): ResponseEntity<FormattedRestaurantTimings> {
        restaurantTimingsValidator.validate(restaurantTimings)
        return ResponseEntity(restaurantTimingsFormatter.format(restaurantTimings), HttpStatus.OK)
    }
}