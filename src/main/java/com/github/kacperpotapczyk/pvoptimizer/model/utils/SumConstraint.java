package com.github.kacperpotapczyk.pvoptimizer.model.utils;

/**
 * Defines sum constraint over intervals range. startInterval has to be lower or equal to endInterval
 * @param startInterval first interval
 * @param endInterval last interval
 * @param sum constraint value
 */
public record SumConstraint(int startInterval, int endInterval, double sum) {

    public SumConstraint {

        if (startInterval > endInterval) {
            throw new IllegalArgumentException(String.format("Sum constraint start interval %d is greater than end interval %d", startInterval, endInterval));
        }
    }
}
