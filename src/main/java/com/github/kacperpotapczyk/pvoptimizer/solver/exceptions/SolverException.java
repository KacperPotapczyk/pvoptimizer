package com.github.kacperpotapczyk.pvoptimizer.solver.exceptions;

/**
 * Exception that {@link com.github.kacperpotapczyk.pvoptimizer.solver.Solver solver} methods can throw.
 */
public class SolverException extends Exception {

    public SolverException(String message) {
        super(message);
    }
}
