package com.algocoach.web;

import com.algocoach.controller.ErrorTestController;
import com.algocoach.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ErrorTestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validationErrorsAreHandled() throws Exception {
        String body = "{\n  \"name\": \"\", \n  \"age\": 0\n}";

        mockMvc.perform(post("/error-test/validation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors").isArray());
    }

    @Test
    void notFoundIsHandled() throws Exception {
        mockMvc.perform(get("/error-test/not-found/123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void businessLogicErrorIsHandled() throws Exception {
        String body = "{ }"; // missing requiredField
        mockMvc.perform(post("/error-test/business-logic")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_LOGIC_ERROR"));
    }

    @Test
    void authErrorIsHandled() throws Exception {
        mockMvc.perform(get("/error-test/auth-error"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_FAILED"));
    }
}


