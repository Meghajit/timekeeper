package com.megh.timekeeper.api

import java.time.DayOfWeek
import java.time.DayOfWeek.*

class RestaurantTimings(
    val sunday: List<OpenCloseTimings>,
    val monday: List<OpenCloseTimings>,
    val tuesday: List<OpenCloseTimings>,
    val wednesday: List<OpenCloseTimings>,
    val thursday: List<OpenCloseTimings>,
    val friday: List<OpenCloseTimings>,
    val saturday: List<OpenCloseTimings>,
) {
    fun getAllDaysData(): Map<DayOfWeek, List<OpenCloseTimings>> {
        val dayMap = HashMap<DayOfWeek, List<OpenCloseTimings>>()
        dayMap[SUNDAY] = this.sunday
        dayMap[MONDAY] = this.monday
        dayMap[TUESDAY] = this.tuesday
        dayMap[WEDNESDAY] = this.wednesday
        dayMap[THURSDAY] = this.thursday
        dayMap[FRIDAY] = this.friday
        dayMap[SATURDAY] = this.saturday
        return dayMap
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return this.hashCode() == other.hashCode()
    }

    override fun hashCode(): Int {
        var result = sunday.hashCode()
        result = 31 * result + monday.hashCode()
        result = 31 * result + tuesday.hashCode()
        result = 31 * result + wednesday.hashCode()
        result = 31 * result + thursday.hashCode()
        result = 31 * result + friday.hashCode()
        result = 31 * result + saturday.hashCode()
        return result
    }
}

data class OpenCloseTimings(val type: RestaurantStatus, val value: Int)

enum class RestaurantStatus {
    open, close
}