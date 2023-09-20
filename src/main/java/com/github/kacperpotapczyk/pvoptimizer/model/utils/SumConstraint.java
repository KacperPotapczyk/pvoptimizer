package com.github.kacperpotapczyk.pvoptimizer.model.utils;

public record SumConstraint(int startInterval, int endInterval, double sum) {

    public SumConstraint {

        if (startInterval > endInterval) {
            throw new IllegalArgumentException(String.format("Sum constraint start interval %d is greater than end interval %d", startInterval, endInterval));
        }
    }
}
