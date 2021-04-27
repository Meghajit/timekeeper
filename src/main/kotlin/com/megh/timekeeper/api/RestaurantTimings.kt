package com.megh.timekeeper.api

data class RestaurantTimings(
    val sunday: List<OpenCloseTimings>,
    val monday: List<OpenCloseTimings>,
    val tuesday: List<OpenCloseTimings>,
    val wednesday: List<OpenCloseTimings>,
    val thursday: List<OpenCloseTimings>,
    val friday: List<OpenCloseTimings>,
    val saturday: List<OpenCloseTimings>,
){
    constructor() : this(emptyList(),emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
}

data class OpenCloseTimings(val type: RestaurantStatus, val timing: Int)

enum class RestaurantStatus {
    OPEN, CLOSE
}