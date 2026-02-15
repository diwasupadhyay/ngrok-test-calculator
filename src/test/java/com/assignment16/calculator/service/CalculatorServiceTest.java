package com.assignment16.calculator.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorServiceTest {

    private final CalculatorService calculatorService = new CalculatorService();

    @Test
    void shouldAddNumbers() {
        assertEquals(9.0, calculatorService.calculate("add", 4, 5.0));
    }

    @Test
    void shouldCalculatePower() {
        assertEquals(8.0, calculatorService.calculate("power", 2, 3.0));
    }

    @Test
    void shouldCalculateFactorial() {
        assertEquals(120.0, calculatorService.calculate("factorial", 5, null));
    }

    @Test
    void shouldThrowOnDivideByZero() {
        assertThrows(IllegalArgumentException.class,
                () -> calculatorService.calculate("divide", 10, 0.0));
    }

    @Test
    void shouldThrowWhenSecondNumberMissing() {
        assertThrows(IllegalArgumentException.class,
                () -> calculatorService.calculate("multiply", 4, null));
    }
}
