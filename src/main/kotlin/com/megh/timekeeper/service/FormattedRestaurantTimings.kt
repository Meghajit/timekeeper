package com.megh.timekeeper.service

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.DayOfWeek
import java.time.DayOfWeek.*

const val CLOSED_STATUS = "Closed"

class FormattedRestaurantTimings(
    @field:JsonProperty("Sunday") private var sunday: String = CLOSED_STATUS,
    @field:JsonProperty("Monday") private var monday: String = CLOSED_STATUS,
    @field:JsonProperty("Tuesday") private var tuesday: String = CLOSED_STATUS,
    @field:JsonProperty("Wednesday") private var wednesday: String = CLOSED_STATUS,
    @field:JsonProperty("Thursday") private var thursday: String = CLOSED_STATUS,
    @field:JsonProperty("Friday") private var friday: String = CLOSED_STATUS,
    @field:JsonProperty("Saturday") private var saturday: String = CLOSED_STATUS
) {

    fun setTimingFromDayOfWeek(day: DayOfWeek, timings: String) {
        when (day) {
            SUNDAY -> this.sunday = timings
            MONDAY -> this.monday = timings
            TUESDAY -> this.tuesday = timings
            WEDNESDAY -> this.wednesday = timings
            THURSDAY -> this.thursday = timings
            FRIDAY -> this.friday = timings
            SATURDAY -> this.saturday = timings
        }
    }
}