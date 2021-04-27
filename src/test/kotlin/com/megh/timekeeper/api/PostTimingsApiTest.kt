package com.megh.timekeeper.api

import com.megh.timekeeper.domain.RestaurantTimingsValidator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.Mockito.`when`
import javax.xml.bind.ValidationException

@WebMvcTest
class PostTimingsApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var restaurantTimingsValidator: RestaurantTimingsValidator

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyIsEmpty_returnsHTTP400() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andContentTypeIsNotSetProperly_returnsHTTP415() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_PDF)
            .content(getSampleHTTP200Request()))
            .andExpect(status().isUnsupportedMediaType)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyHasMalformedJSON_returnsHTTP400() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON)
            .content("This is not a JSON request body"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasMissingDaysOfWeek_returnsHTTP422() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getSampleMissingDaysOfWeekRequest()))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasAtLeastOneDayWithNullOpeningHours_returnsHTTP422() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getSampleNullOpeningHoursRequest()))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasNonEmptyOpeningHoursOnAtLeastOneDay_returnsHTTP200() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getSampleAtLeastOneNonEmptyOpeningHoursRequest()))
            .andExpect(status().isOk)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestHasAllEmptyOpeningHours_returnsHTTP422() {
        `when`(restaurantTimingsValidator.validate(RestaurantTimings())).thenThrow(ValidationException("validation error"))

        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getSampleAllEmptyOpeningHoursRequest()))
            .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun whenPostTimingsAPIIsCalled_andRequestBodyIsValid_returnsHTTP200() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getSampleHTTP200Request()))
            .andExpect(status().isOk)
    }
}