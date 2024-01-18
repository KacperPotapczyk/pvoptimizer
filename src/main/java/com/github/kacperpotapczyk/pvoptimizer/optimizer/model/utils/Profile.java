package com.github.kacperpotapczyk.pvoptimizer.optimizer.model.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * List of consecutive values that have initial interval corresponding to {@link com.github.kacperpotapczyk.pvoptimizer.optimizer.model.Task Task}
 * or {@link com.github.kacperpotapczyk.pvoptimizer.optimizer.model.Result Result} intervals.
 */
@Getter
@RequiredArgsConstructor
public class Profile {

    /**
     * Initial interval.
     */
    private final int startInterval;
    /**
     * List of consecutive values.
     */
    private final List<Double> values;

    /**
     * Profile with start interval equals 0.
     * @param values list of consecutive values
     */
    public Profile(List<Double> values) {
        this.startInterval = 0;
        this.values = values;
    }

    /**
     * Constant profile with start interval equals 0 and defined length.
     * @param length number of profile values
     * @param constantValue constant value
     */
    public Profile(int length, Double constantValue) {
        this.startInterval = 0;
        values = new ArrayList<>(length);
        for (int i=0; i<length; i++) {
            values.add(constantValue);
        }
    }

    /**
     * Constant profile with defined start interval and length.
     * @param startInterval first interval of profile
     * @param length number of profile values
     * @param constantValue constant value
     */
    public Profile(int startInterval, int length, Double constantValue) {
        this.startInterval = startInterval;
        values = new ArrayList<>(length);
        for (int i=0; i<length; i++) {
            values.add(constantValue);
        }
    }

    /**
     * Returns profile length in intervals.
     * @return profile length
     */
    public int getLength() {
        return values.size();
    }

    /**
     * Returns last interval of profile.
     * @return last interval of profile
     */
    public int getLastInterval() {
        return this.getStartInterval() + this.getLength();
    }

    /**
     * Returns profile value for internal profile index
     * @param index internal index
     * @return value for given index or null if index does not exist in profile
     */
    public Optional<Double> getValueForIndex(int index) {
        if (isIndexValid(index)) {
            return Optional.of(values.get(index));
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns profile value at given interval.
     * @param interval optimization task interval
     * @return value for given interval or null if interval does not exist in profile
     */
    public Optional<Double> getValueForInterval(int interval) {
        int index = interval - this.getStartInterval();
        return this.getValueForIndex(index);
    }

    private boolean isIndexValid(int index) {
        return index >=0 && index < this.getLength();
    }
}
