package com.github.kacperpotapczyk.pvoptimizer.solver.enums;

import com.github.kacperpotapczyk.pvoptimizer.solver.Solver;

/**
 * Possible statuses that {@link Solver#solve() solve} method can return.
 */
public enum SolutionStatus {
    /**
     * Solution is found and relative gap is lower than set value.
     */
    OPTIMAL,
    /**
     * Suboptimal solution is found, computation is stopped due to time limit.
     */
    SUBOPTIMAL,
    /**
     * Solution is not found in given time limit but model is feasible.
     */
    TIMEOUT,
    /**
     * Model is infeasible.
     */
    INFEASIBLE,
    /**
     * Optimal variables values goes to infinity.
     */
    UNBOUNDED,
    /**
     * Error during optimization occurred.
     */
    ERROR
}
