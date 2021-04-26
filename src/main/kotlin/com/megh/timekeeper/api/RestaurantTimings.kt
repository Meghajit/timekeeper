package com.megh.timekeeper.api

class RestaurantTimings(
    val sunday: List<OpenCloseTimings> = emptyList(),
    val monday: List<OpenCloseTimings> = emptyList(),
    val tuesday: List<OpenCloseTimings> = emptyList(),
    val wednesday: List<OpenCloseTimings> = emptyList(),
    val thursday: List<OpenCloseTimings> = emptyList(),
    val friday: List<OpenCloseTimings> = emptyList(),
    val saturday: List<OpenCloseTimings> = emptyList(),
)

data class OpenCloseTimings(val type: RestaurantStatus, val timing: Int)

enum class RestaurantStatus {
    OPEN, CLOSE
}