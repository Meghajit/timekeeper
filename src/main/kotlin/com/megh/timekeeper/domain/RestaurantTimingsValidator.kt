package com.megh.timekeeper.domain

import com.megh.timekeeper.api.OpenCloseTimings
import com.megh.timekeeper.api.RestaurantStatus.close
import com.megh.timekeeper.api.RestaurantStatus.open
import com.megh.timekeeper.api.RestaurantTimings
import org.springframework.stereotype.Component
import javax.xml.bind.ValidationException
import kotlin.jvm.Throws

@Component
class RestaurantTimingsValidator {
    companion object {
        private fun checkIfInvalidTimings(timings: List<OpenCloseTimings>): Boolean {
            return !(timings.isEmpty() || timings.all { it.value in 0..86399 })
        }
    }

    @Throws(ValidationException::class)
    fun validate(restaurantTimings: RestaurantTimings) {
        validateTimingsForAllDaysAreNotEmpty(restaurantTimings)
        validateTimingsAreWithinLimits(restaurantTimings)
        validateTimingsAreInPairs(restaurantTimings)
    }

    @Throws
    private fun validateTimingsAreInPairs(restaurantTimings: RestaurantTimings) {
        var openStatusCount = 0
        var closeStatusCount = 0
        restaurantTimings.getAllDaysData().forEach { openCloseTimingsForDay ->
            openCloseTimingsForDay.forEach { openCloseTimings ->
                when (openCloseTimings.type) {
                    open -> openStatusCount++
                    close -> closeStatusCount++
                }
            }
        }

        if(openStatusCount!=closeStatusCount) {
            throw ValidationException("TIMEKEEPER_VALIDATION_EXCEPTION_OPENING_HOURS_NOT_COMPLEMENTARY")
        }
    }

    @Throws(ValidationException::class)
    private fun validateTimingsAreWithinLimits(restaurantTimings: RestaurantTimings) {
        if (checkIfInvalidTimings(restaurantTimings.sunday) ||
            checkIfInvalidTimings(restaurantTimings.monday) ||
            checkIfInvalidTimings(restaurantTimings.tuesday) ||
            checkIfInvalidTimings(restaurantTimings.wednesday) ||
            checkIfInvalidTimings(restaurantTimings.thursday) ||
            checkIfInvalidTimings(restaurantTimings.friday) ||
            checkIfInvalidTimings(restaurantTimings.saturday)
        ) {
            throw ValidationException("TIMEKEEPER_VALIDATION_EXCEPTION_TIMINGS_OUT_OF_RANGE")
        }
    }

    @Throws(ValidationException::class)
    private fun validateTimingsForAllDaysAreNotEmpty(restaurantTimings: RestaurantTimings) {
        if (restaurantTimings.sunday.isEmpty() &&
            restaurantTimings.monday.isEmpty() &&
            restaurantTimings.tuesday.isEmpty() &&
            restaurantTimings.wednesday.isEmpty() &&
            restaurantTimings.thursday.isEmpty() &&
            restaurantTimings.friday.isEmpty() &&
            restaurantTimings.saturday.isEmpty()
        ) {
            throw ValidationException("TIMEKEEPER_VALIDATION_EXCEPTION_ALL_DAYS_TIMINGS_EMPTY")
        }
    }
}