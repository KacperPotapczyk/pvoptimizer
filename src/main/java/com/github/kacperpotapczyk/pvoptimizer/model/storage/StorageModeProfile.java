package com.github.kacperpotapczyk.pvoptimizer.model.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Profile of storage operation modes. Special case of {@link com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile}.
 */
@Getter
@RequiredArgsConstructor
public class StorageModeProfile {

    /**
     * Initial interval
     */
    private final int startInterval;
    /**
     * List of consecutive operation modes.
     */
    private final List<StorageMode> values;

    /**
     * Profile with start interval equals 0.
     * @param values list of consecutive operation modes
     */
    public StorageModeProfile(List<StorageMode> values) {
        this.startInterval = 0;
        this.values = values;
    }

    /**
     * Constant profile with start interval equals 0 and defined length.
     * @param length number of profile values
     * @param constantValue constant mode
     */
    public StorageModeProfile(int length, StorageMode constantValue) {
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
     * @param constantValue constant mode
     */
    public StorageModeProfile(int startInterval, int length, StorageMode constantValue) {
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
     * Returns mode for internal profile index
     * @param index internal index
     * @return mode for given index or null if index does not exist in profile
     */
    public Optional<StorageMode> getValueForIndex(int index) {
        if (isIndexValid(index)) {
            return Optional.of(values.get(index));
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns mode value at given interval.
     * @param interval optimization task interval
     * @return mode for given interval or null if interval does not exist in profile
     */
    public Optional<StorageMode> getValueForInterval(int interval) {
        int index = interval - this.getStartInterval();
        return this.getValueForIndex(index);
    }

    private boolean isIndexValid(int index) {
        return index >=0 && index < this.getLength();
    }
}
