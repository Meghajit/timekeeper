package com.megh.timekeeper.service

import com.megh.timekeeper.api.RestaurantStatus.close
import com.megh.timekeeper.api.RestaurantStatus.open
import com.megh.timekeeper.api.RestaurantTimings
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

@Service
class RestaurantTimingsFormatterImpl : RestaurantTimingsFormatter {

    override fun format(restaurantTimings: RestaurantTimings): Any {
        val allDaysData = restaurantTimings.getAllDaysData()
        val formattedRestaurantTimings = FormattedRestaurantTimings()

        allDaysData.forEach { (dayOfTheWeek, timingsForDay) ->
            val openingTimesForDay = timingsForDay.filter { it.type == open }

            if (openingTimesForDay.isNotEmpty()) {
                val closingTimesForDay = timingsForDay.filter { it.type == close }
                val formattedTimingsForDay = ArrayList<String>()
                openingTimesForDay.forEach { openingTime ->

                    val closingTime = closingTimesForDay.find { it.value > openingTime.value }?.value
                        ?: allDaysData[dayOfTheWeek + 1]?.minByOrNull { it.value }?.value
                    formattedTimingsForDay.add(constructFormattedOpenHours(openingTime.value, closingTime!!))
                }

                formattedRestaurantTimings.setTimingFromDayOfWeek(
                    dayOfTheWeek,
                    formattedTimingsForDay.joinToString(", ")
                )
            }
        }
        return formattedRestaurantTimings
    }

    private fun constructFormattedOpenHours(openingTimeInEpochSeconds: Int, closingTimeInEpochSeconds: Int): String {
        val timeFormat = DateTimeFormatter.ofPattern("h:mm a")
        val openingTime = LocalDateTime.ofEpochSecond(openingTimeInEpochSeconds.toLong(), 0, ZoneOffset.UTC)
            .format(timeFormat).replace(":00", "")
        val closingTime = LocalDateTime.ofEpochSecond(closingTimeInEpochSeconds.toLong(), 0, ZoneOffset.UTC)
            .format(timeFormat).replace(":00", "")
        return "$openingTime - $closingTime"
    }
}