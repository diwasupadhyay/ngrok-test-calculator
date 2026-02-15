package com.assignment16.calculator.controller;

import com.assignment16.calculator.service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculatorService calculatorService;

    @Test
    void shouldReturnCalculationResult() throws Exception {
        when(calculatorService.calculate("add", 2.0, 3.0)).thenReturn(5.0);

        mockMvc.perform(get("/api/calculate")
                        .param("operation", "add")
                        .param("a", "2")
                        .param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    void shouldReturnBadRequestOnIllegalArgument() throws Exception {
        when(calculatorService.calculate("divide", 4.0, 0.0))
                .thenThrow(new IllegalArgumentException("Cannot divide by zero."));

        mockMvc.perform(get("/api/calculate")
                        .param("operation", "divide")
                        .param("a", "4")
                        .param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cannot divide by zero."));
    }
}
