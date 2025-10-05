package com.algocoach.web;

import com.algocoach.aspect.RateLimitingAspect;
import com.algocoach.config.RateLimitingConfig;
import com.algocoach.controller.ErrorTestController;
import com.algocoach.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ErrorTestController.class)
@Import({GlobalExceptionHandler.class, RateLimitingAspect.class, RateLimitingConfig.class})
class RateLimitingWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exceedingRateLimitReturns429() throws Exception {
        // First two requests succeed
        mockMvc.perform(get("/error-test/rate-limit"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/error-test/rate-limit"))
                .andExpect(status().isOk());

        // Third should be rate limited
        mockMvc.perform(get("/error-test/rate-limit"))
                .andExpect(status().isTooManyRequests());
    }
}


