package com.github.kacperpotapczyk.pvoptimizer.service;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;

/**
 * Service responsible for creating model based on received {@link Task task} data
 * and then invoking {@link com.github.kacperpotapczyk.pvoptimizer.solver.Solver solver} to find optimal solution.
 * Optimization goal is to find optimal electric energy purchase, sell and storage strategy to minimize operation costs
 * while fulfilling fixed PV generation and household electricity demand profiles.
 */
public interface Optimizer {

    /**
     * Creates model based on received data and then solves optimization problem.
     * @param task model data
     * @return results for given task
     */
    Result solve(Task task);
}
