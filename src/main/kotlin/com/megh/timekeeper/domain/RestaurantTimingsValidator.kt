package com.megh.timekeeper.domain

import com.megh.timekeeper.api.OpenCloseTimings
import com.megh.timekeeper.api.RestaurantStatus.close
import com.megh.timekeeper.api.RestaurantStatus.open
import com.megh.timekeeper.api.RestaurantTimings
import org.springframework.stereotype.Component
import java.time.DayOfWeek
import javax.xml.bind.ValidationException
import kotlin.jvm.Throws

@Component
class RestaurantTimingsValidator {
    @Throws(ValidationException::class)
    fun validate(restaurantTimings: RestaurantTimings) {
        validateTimingsForAllDaysAreNotEmpty(restaurantTimings)
        validateTimingsAreWithinLimits(restaurantTimings)
        validateTimingsAreInPairs(restaurantTimings)
        validateOpenAndCloseTimingsAreOnSameDayOrNextDay(restaurantTimings)
    }

    @Throws(ValidationException::class)
    private fun validateOpenAndCloseTimingsAreOnSameDayOrNextDay(restaurantTimings: RestaurantTimings) {
        val dayMap = restaurantTimings.getAllDaysData()
        dayMap.forEach { (day, openCloseTimings) ->
            if (openCloseTimings.isNotEmpty()) {
                val nextDay: DayOfWeek = day + 1
                val timingsForNextDay = dayMap[nextDay]!!
                if (checkIfInvalidChronologicalOrderOfNextDayCloseTimings(openCloseTimings, timingsForNextDay)) {
                    throw ValidationException("TIMEKEEPER_VALIDATION_EXCEPTION_INVALID_CHRONOLOGICAL_ORDER_OF_OPENING_HOURS")
                }
            }
        }
    }

    @Throws
    private fun validateTimingsAreInPairs(restaurantTimings: RestaurantTimings) {
        var openStatusCount = 0
        var closeStatusCount = 0
        restaurantTimings.getAllDaysData().forEach { openCloseTimingsForDay ->
            openStatusCount += openCloseTimingsForDay.value.filter { it.type == open }.size
            closeStatusCount += openCloseTimingsForDay.value.filter { it.type == close }.size
        }
        if (openStatusCount != closeStatusCount) {
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

    private fun checkIfInvalidTimings(timings: List<OpenCloseTimings>): Boolean {
        return !(timings.isEmpty() || timings.all { it.value in 0..86399 })
    }

    private fun checkIfInvalidChronologicalOrderOfNextDayCloseTimings(
        currentDayTimings: List<OpenCloseTimings>, nextDayTimings: List<OpenCloseTimings>
    ): Boolean {
        val currentDayOpenTimings = currentDayTimings.filter { it.type == open }
        val currentDayCloseTimings = currentDayTimings.filter { it.type == close }
        val nextDayFirstStatus = nextDayTimings.minByOrNull { it.value }?.type

        if (currentDayTimings.isEmpty()) {
            return false
        } else {
            currentDayOpenTimings.forEach { openingTime ->
                if (currentDayCloseTimings.none { it.value > openingTime.value } && nextDayFirstStatus != close) {
                    return true
                }
            }
            return false
        }
    }
}