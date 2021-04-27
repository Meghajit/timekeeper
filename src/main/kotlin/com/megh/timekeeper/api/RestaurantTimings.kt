package com.megh.timekeeper.api

class RestaurantTimings(
    val sunday: List<OpenCloseTimings>,
    val monday: List<OpenCloseTimings>,
    val tuesday: List<OpenCloseTimings>,
    val wednesday: List<OpenCloseTimings>,
    val thursday: List<OpenCloseTimings>,
    val friday: List<OpenCloseTimings>,
    val saturday: List<OpenCloseTimings>,
) {
    fun getAllDaysData():List<List<OpenCloseTimings>> {
        return listOf(sunday, monday, tuesday, wednesday, thursday, friday, saturday)
    }
}

data class OpenCloseTimings(val type: RestaurantStatus, val value: Int)

enum class RestaurantStatus {
    open, close
}