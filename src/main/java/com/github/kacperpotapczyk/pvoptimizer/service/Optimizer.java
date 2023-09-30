package com.github.kacperpotapczyk.pvoptimizer.service;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;

/**
 * Optimizer interface.
 */
public interface Optimizer {

    /**
     * Creates model based on received data and then solves optimization problem.
     * @param task model data
     * @return results for given task
     */
    Result solve(Task task);
}
