package com.megh.timekeeper.domain

import com.megh.timekeeper.api.RestaurantTimings
import org.springframework.stereotype.Component
import javax.xml.bind.ValidationException
import kotlin.jvm.Throws

@Component
class RestaurantTimingsValidator {

    @Throws(ValidationException::class)
    fun validate(restaurantTimings: RestaurantTimings) {
         validateTimingsForAllDaysAreNotEmpty(restaurantTimings)
    }

    @Throws(ValidationException::class)
    private fun validateTimingsForAllDaysAreNotEmpty(restaurantTimings: RestaurantTimings) {
        if(restaurantTimings.sunday.isEmpty() ||
               restaurantTimings.monday.isEmpty() ||
               restaurantTimings.tuesday.isEmpty() ||
               restaurantTimings.wednesday.isEmpty() ||
               restaurantTimings.thursday.isEmpty() ||
               restaurantTimings.friday.isEmpty() ||
               restaurantTimings.saturday.isEmpty()) {
            throw ValidationException("TIMEKEEPER_VALIDATION_EXCEPTION_ALL_DAYS_TIMINGS_EMPTY")
        }
    }
}