package com.megh.timekeeper.service

import com.megh.timekeeper.api.RestaurantStatus.close
import com.megh.timekeeper.api.RestaurantStatus.open
import com.megh.timekeeper.api.RestaurantTimings
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

/** This class implements RestaurantTimingsFormatter and formats the restaurant timings into a human readable version
 * using 12-hour clock.
 * The format function iterates over the days of the week. For each day, it checks if there are any timings. If not,
 * it skips the day.
 * If timings are present for the day, it iterates over the timings of the day with type as
 * `open` and tries to find a complementary closing time for it.
 * For finding the closing time, it first checks if a closing time exists on the same day at a time after
 * the opening time. If not, it takes the first event of the next day as the closing time since the data is
 * already supposed to be validated at an upper layer.
 * The opening closing time is then eventually formatted using a helper function. **/

@Service
class RestaurantTimingsFormatterImpl : RestaurantTimingsFormatter {

    override fun format(restaurantTimings: RestaurantTimings): FormattedRestaurantTimings {
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