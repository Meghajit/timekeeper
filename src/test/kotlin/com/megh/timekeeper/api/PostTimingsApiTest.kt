package com.megh.timekeeper.api

import com.megh.timekeeper.service.FormattedRestaurantTimings
import com.megh.timekeeper.service.RestaurantTimingsFormatterImpl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class PostTimingsApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var restaurantTimingsFormatter: RestaurantTimingsFormatterImpl

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyIsEmpty_returnsHTTP400() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andContentTypeIsNotSetProperly_returnsHTTP415() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_PDF)
                .content(getSampleHTTP200Request())
        )
            .andExpect(status().isUnsupportedMediaType)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyHasMalformedJSON_returnsHTTP400() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content("This is not a JSON request body")
        )
            .andExpect(status().isBadRequest)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasMissingDaysOfWeek_returnsHTTP422() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleMissingDaysOfWeekRequest())
        )
            .andExpect(status().isUnprocessableEntity)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasAtLeastOneDayWithNullOpeningHours_returnsHTTP422() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleNullOpeningHoursRequest())
        )
            .andExpect(status().isUnprocessableEntity)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasAllEmptyOpeningHours_returnsHTTP422() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleAllEmptyOpeningHoursRequest())
        )
            .andExpect(status().isUnprocessableEntity)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyHasInvalidOpeningHours_returnsHTTP422() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleInvalidOpeningHoursRequest())
        )
            .andExpect(status().isUnprocessableEntity)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyHasUnParsableOpeningHours_returns400() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleUnParsableOpeningHoursRequest())
        )
            .andExpect(status().isBadRequest)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyHasUnParsableOpeningHoursType_returns400() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleUnParsableOpeningHoursTypeRequest())
        )
            .andExpect(status().isBadRequest)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyDoesNotHaveOpenCloseTimingsInPairsAcrossDays_returns422() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleNotPairedOpeningHoursRequest())
        )
            .andExpect(status().isUnprocessableEntity)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyHasIncorrectChronologicalOrderOfOpenAndCloseTimingsOnSameDay_returns422() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleIncorrectChronologicalOrderOfOpenAndCloseTimingsOnSameDayRequest())
        )
            .andExpect(status().isUnprocessableEntity)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyHasIncorrectChronologicalOrderOfNextDayCloseTimings_returns422() {
        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleIncorrectChronologicalOrderOfCloseTimingsOnNextDayRequest())
        )
            .andExpect(status().isUnprocessableEntity)
        verifyNoInteractions(restaurantTimingsFormatter)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasNonEmptyOpeningHoursOnAtLeastOneDay_returnsHTTP200() {
        val restaurantTimings = RestaurantTimings(
            sunday = emptyList(),
            monday = listOf(OpenCloseTimings(RestaurantStatus.open, 2343), OpenCloseTimings(RestaurantStatus.close, 4500)),
            tuesday = emptyList(),
            wednesday = emptyList(),
            thursday = emptyList(),
            friday = emptyList(),
            saturday = emptyList()
        )
        val expectedResponse = FormattedRestaurantTimings(
            monday = "12:39 AM - 1:15 AM",
        )
        `when`(restaurantTimingsFormatter.format(restaurantTimings)).thenReturn(expectedResponse)

        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleAtLeastOneNonEmptyOpeningHoursRequest())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.Sunday").value("Closed"))
            .andExpect(jsonPath("$.Monday").value("12:39 AM - 1:15 AM"))
            .andExpect(jsonPath("$.Tuesday").value("Closed"))
            .andExpect(jsonPath("$.Wednesday").value("Closed"))
            .andExpect(jsonPath("$.Thursday").value("Closed"))
            .andExpect(jsonPath("$.Friday").value("Closed"))
            .andExpect(jsonPath("$.Saturday").value("Closed"))

    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyIsValid_returnsHTTP200() {
        val restaurantTimings = RestaurantTimings(
            sunday = emptyList(),
            monday = listOf(OpenCloseTimings(RestaurantStatus.open, 1200), OpenCloseTimings(RestaurantStatus.close, 8000), OpenCloseTimings(
                RestaurantStatus.open, 80000)),
            tuesday = listOf(
                OpenCloseTimings(RestaurantStatus.close, 1200),
                OpenCloseTimings(RestaurantStatus.open, 6000),
                OpenCloseTimings(RestaurantStatus.close, 12000)
            ),
            wednesday = emptyList(),
            thursday = emptyList(),
            friday = listOf(OpenCloseTimings(RestaurantStatus.open, 1200)),
            saturday = listOf(OpenCloseTimings(RestaurantStatus.close, 8000))
        )
        val expectedResponse = FormattedRestaurantTimings(
            monday = "12:30 AM - 8 AM, 10 PM - 1 AM",
            tuesday = "8 AM - 10 PM",
            friday = "10 PM - 1 AM"
        )
        `when`(restaurantTimingsFormatter.format(restaurantTimings)).thenReturn(expectedResponse)

        mockMvc.perform(
            post("/v1/timings/format")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getSampleHTTP200Request())
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.Sunday").value("Closed"))
            .andExpect(jsonPath("$.Monday").value("12:30 AM - 8 AM, 10 PM - 1 AM"))
            .andExpect(jsonPath("$.Tuesday").value("8 AM - 10 PM"))
            .andExpect(jsonPath("$.Wednesday").value("Closed"))
            .andExpect(jsonPath("$.Thursday").value("Closed"))
            .andExpect(jsonPath("$.Friday").value("10 PM - 1 AM"))
            .andExpect(jsonPath("$.Saturday").value("Closed"))
    }
}