package com.github.kacperpotapczyk.pvoptimizer.optimizer.service.optimizer;

import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.Task;

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
