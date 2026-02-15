package com.assignment16.calculator.controller;

import com.assignment16.calculator.service.CalculatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CalculatorController {

    private final CalculatorService calculatorService;

    public CalculatorController(CalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculate(
            @RequestParam String operation,
            @RequestParam double a,
            @RequestParam(required = false) Double b
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            double result = calculatorService.calculate(operation, a, b);
            response.put("operation", operation);
            response.put("a", a);
            response.put("b", b);
            response.put("result", result);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            response.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
