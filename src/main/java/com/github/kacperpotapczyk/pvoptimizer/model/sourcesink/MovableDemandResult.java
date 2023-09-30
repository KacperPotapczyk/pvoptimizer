package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

/**
 * Optimization result of movable demand start interval adjustment
 * @param id movable demand id
 * @param name movable demand name
 * @param startInterval optimal start interval
 */
public record MovableDemandResult(int id, String name, int startInterval) {
}
