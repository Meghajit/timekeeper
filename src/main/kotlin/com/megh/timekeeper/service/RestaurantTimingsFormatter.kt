package com.megh.timekeeper.service

import com.megh.timekeeper.api.RestaurantTimings

interface RestaurantTimingsFormatter {
    fun format(restaurantTimings: RestaurantTimings):FormattedRestaurantTimings
}
