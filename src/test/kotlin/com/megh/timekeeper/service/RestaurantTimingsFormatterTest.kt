package com.megh.timekeeper.service

import com.megh.timekeeper.api.OpenCloseTimings
import com.megh.timekeeper.api.RestaurantStatus.close
import com.megh.timekeeper.api.RestaurantStatus.open
import com.megh.timekeeper.api.RestaurantTimings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RestaurantTimingsFormatterTest {

    private val restaurantTimingsFormatter = RestaurantTimingsFormatterImpl()

    @Test
    fun whenFormatMethodIsCalledWithRestaurantTimingsAsArgs_formatsAndReturnsTheTimingsInHumanReadableFormat() {
        val restaurantTimings = RestaurantTimings(
            sunday = emptyList(),
            monday = listOf(OpenCloseTimings(open, 1800), OpenCloseTimings(close, 28800), OpenCloseTimings(open, 79200)),
            tuesday = listOf(
                OpenCloseTimings(close, 3600),
                OpenCloseTimings(open, 28800),
                OpenCloseTimings(close, 79200)
            ),
            wednesday = emptyList(),
            thursday = emptyList(),
            friday = listOf(OpenCloseTimings(open, 79200)),
            saturday = listOf(OpenCloseTimings(close, 3600))
        )
        val expectedResponse = FormattedRestaurantTimings(
            monday = "12:30 AM - 8 AM, 10 PM - 1 AM",
            tuesday = "8 AM - 10 PM",
            friday = "10 PM - 1 AM"
        )

        val response = restaurantTimingsFormatter.format(restaurantTimings)

        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse)
    }
}