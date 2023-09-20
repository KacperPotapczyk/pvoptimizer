package com.github.kacperpotapczyk.pvoptimizer.model.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class Profile {

    private final int startInterval;
    private final List<Double> values;

    public Profile(List<Double> values) {
        this.startInterval = 0;
        this.values = values;
    }

    public Profile(int length, Double constantValue) {
        this.startInterval = 0;
        values = new ArrayList<>(length);
        for (int i=0; i<length; i++) {
            values.add(constantValue);
        }
    }

    public Profile(int startInterval, int length, Double constantValue) {
        this.startInterval = startInterval;
        values = new ArrayList<>(length);
        for (int i=0; i<length; i++) {
            values.add(constantValue);
        }
    }

    public int getLength() {
        return values.size();
    }

    public int getLastInterval() {
        return this.getStartInterval() + this.getLength();
    }

    public Optional<Double> getValueForIndex(int index) {
        if (isIndexValid(index)) {
            return Optional.of(values.get(index));
        }
        else {
            return Optional.empty();
        }
    }

    public Optional<Double> getValueForInterval(int interval) {
        int index = interval - this.getStartInterval();
        return this.getValueForIndex(index);
    }

    private boolean isIndexValid(int index) {
        return index >=0 && index < this.getLength();
    }
}
