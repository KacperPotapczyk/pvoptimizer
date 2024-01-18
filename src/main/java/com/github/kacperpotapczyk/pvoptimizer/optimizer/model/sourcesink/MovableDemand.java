package com.github.kacperpotapczyk.pvoptimizer.optimizer.model.sourcesink;

import java.util.List;
import java.util.Set;

/**
 * Defines demand that has fixed profile but its starting interval can be adjusted by optimizer.
 * @param id movable demand id
 * @param name movable demand name
 * @param profile demand profile
 * @param startIntervals set of start intervals from which optimizer can choose
 */
public record MovableDemand(long id, String name, List<Double> profile, Set<Integer> startIntervals) {

    /**
     * Two movable demands are consider equal when they have the same id.
     * @param o movable demand
     * @return if movable demands are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovableDemand that)) return false;

        return id() == that.id();
    }

    /**
     * Two movable demands are consider equal when they have the same id.
     * @return movable demand id
     */
    @Override
    public int hashCode() {
        return Long.hashCode(id());
    }
}
