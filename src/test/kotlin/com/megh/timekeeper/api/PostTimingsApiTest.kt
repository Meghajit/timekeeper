package com.megh.timekeeper.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest
class PostTimingsApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

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
    fun whenPostTimingsAPIIsCalled_andRequestBodyIsInvalid_returnsHTTP422() {
        mockMvc.perform(post("/v1/timings/format")
            .contentType(MediaType.APPLICATION_JSON)
            .content(getSampleHTTP422Request()))
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