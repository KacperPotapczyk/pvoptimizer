package com.github.kacperpotapczyk.pvoptimizer.model.utils;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;

/**
 * Available optimization statuses.
 */
public enum OptimizationStatus {
    /**
     * Solution is found. To determine solution quality check results {@link Result#getRelativeGap() relative gap}.
     */
    SOLUTION_FOUND,
    /**
     * Solution is not found. Check {@link Result#getErrorMessage() error message}.
     */
    SOLUTION_NOT_FOUND
}
