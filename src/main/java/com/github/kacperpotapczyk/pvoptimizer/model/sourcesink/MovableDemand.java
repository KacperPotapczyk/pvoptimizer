package com.github.kacperpotapczyk.pvoptimizer.model.sourcesink;

import java.util.List;
import java.util.Set;

public record MovableDemand(int id, String name, List<Double> profile, Set<Integer> startIntervals) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MovableDemand that)) return false;

        return id() == that.id();
    }

    @Override
    public int hashCode() {
        return id();
    }
}
