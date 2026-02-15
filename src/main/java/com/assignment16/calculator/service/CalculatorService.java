package com.assignment16.calculator.service;

import org.springframework.stereotype.Service;

@Service
public class CalculatorService {

    public double calculate(String operation, double a, Double b) {
        return switch (operation.toLowerCase()) {
            case "add" -> a + requireSecondValue(b);
            case "subtract" -> a - requireSecondValue(b);
            case "multiply" -> a * requireSecondValue(b);
            case "divide" -> divide(a, requireSecondValue(b));
            case "power" -> Math.pow(a, requireSecondValue(b));
            case "sqrt" -> sqrt(a);
            case "sin" -> Math.sin(Math.toRadians(a));
            case "cos" -> Math.cos(Math.toRadians(a));
            case "tan" -> Math.tan(Math.toRadians(a));
            case "log" -> log10(a);
            case "ln" -> ln(a);
            case "factorial" -> factorial(a);
            default -> throw new IllegalArgumentException("Unsupported operation: " + operation);
        };
    }

    private double requireSecondValue(Double b) {
        if (b == null) {
            throw new IllegalArgumentException("Second value is required for this operation.");
        }
        return b;
    }

    private double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero.");
        }
        return a / b;
    }

    private double sqrt(double a) {
        if (a < 0) {
            throw new IllegalArgumentException("Square root of negative number is not allowed.");
        }
        return Math.sqrt(a);
    }

    private double log10(double a) {
        if (a <= 0) {
            throw new IllegalArgumentException("Logarithm input must be greater than zero.");
        }
        return Math.log10(a);
    }

    private double ln(double a) {
        if (a <= 0) {
            throw new IllegalArgumentException("Natural log input must be greater than zero.");
        }
        return Math.log(a);
    }

    private double factorial(double a) {
        if (a < 0 || a != Math.floor(a)) {
            throw new IllegalArgumentException("Factorial is only defined for non-negative integers.");
        }

        long n = (long) a;
        long result = 1;
        for (long i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
