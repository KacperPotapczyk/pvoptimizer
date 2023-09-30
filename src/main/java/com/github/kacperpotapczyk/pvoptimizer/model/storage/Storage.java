package com.github.kacperpotapczyk.pvoptimizer.model.storage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Storage definition required for optimization model. It is assumed that storage is available at all task intervals.
 */
@Getter
@RequiredArgsConstructor
public class Storage {

    /**
     * Storage id
     */
    private final int id;
    /**
     * Storage name
     */
    private final String name;
    /**
     * Maximal charge power
     */
    private final double maxCharge;
    /**
     * Maximal discharge power
     */
    private final double maxDischarge;
    /**
     * Maximal energy stored.
     */
    private final double maxCapacity;
    /**
     * Energy stored at 0 interval
     */
    private double initialEnergy;
    /**
     * Interval and interval minimal charge power value constraint pairs
     */
    private Map<Integer, Double> minChargeConstraints;
    /**
     * Interval and interval maximal charge power value constraints pairs
     */
    private Map<Integer, Double> maxChargeConstraints;
    /**
     * Interval and interval minimal discharge power value constraint pairs
     */
    private Map<Integer, Double> minDischargeConstraints;
    /**
     * Interval and interval maximal discharge power value constraints pairs
     */
    private Map<Integer, Double> maxDischargeConstraints;
    /**
     * Interval and interval minimal energy stored value constraints pairs
     */
    private Map<Integer, Double> minEnergyConstraints;
    /**
     * Interval and interval maximal energy stored value constraints pairs
     */
    private Map<Integer, Double> maxEnergyConstraints;
    /**
     * Intervals at which storage charging is forbidden
     */
    private Set<Integer> forbiddenChargeIntervals;
    /**
     * Intervals at which storage discharging is forbidden
     */
    private Set<Integer> forbiddenDischargeIntervals;

    /**
     * Storage builder with required fields
     * @param id storage id
     * @param name storage name
     * @param maxCharge maximal charge power
     * @param maxDischarge maximal discharge power
     * @param maxCapacity maximal energy stored
     * @return storage builder
     */
    public static StorageBuilder builder(int id, String name, double maxCharge, double maxDischarge, double maxCapacity) {
        return new StorageBuilder(id, name, maxCharge, maxDischarge, maxCapacity);
    }

    private boolean setInitialEnergy(double initialEnergy) {

        if (initialEnergy >= 0 && initialEnergy <= maxCapacity) {
            this.initialEnergy = initialEnergy;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMinChargeConstraints(Map<Integer, Double> minChargeConstraints) {

        if (isChargeConstraintsValid(minChargeConstraints)) {
            this.minChargeConstraints = minChargeConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMaxChargeConstraints(Map<Integer, Double> maxChargeConstraints) {

        if (isChargeConstraintsValid(maxChargeConstraints)) {
            this.maxChargeConstraints = maxChargeConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMinDischargeConstraints(Map<Integer, Double> minDischargeConstraints) {

        if (isDischargeConstraintsValid(minDischargeConstraints)) {
            this.minDischargeConstraints = minDischargeConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMaxDischargeConstraints(Map<Integer, Double> maxDischargeConstraints) {

        if (isDischargeConstraintsValid(maxDischargeConstraints)) {
            this.maxDischargeConstraints = maxDischargeConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isChargeConstraintsValid(Map<Integer, Double> powerConstraints) {
        return powerConstraints.values().stream()
                .allMatch(value -> value <= maxCharge);
    }

    private boolean isDischargeConstraintsValid(Map<Integer, Double> powerConstraints) {
        return powerConstraints.values().stream()
                .allMatch(value -> value <= maxDischarge);
    }

    private boolean setMinEnergyConstraints(Map<Integer, Double> minEnergyConstraints) {

        if (isEnergyConstraintsValid(minEnergyConstraints)) {
            this.minEnergyConstraints = minEnergyConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMaxEnergyConstraints(Map<Integer, Double> maxEnergyConstraints) {

        if (isEnergyConstraintsValid(maxEnergyConstraints)) {
            this.maxEnergyConstraints = maxEnergyConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean isEnergyConstraintsValid(Map<Integer, Double> energyConstraints) {
        return energyConstraints.values().stream()
                .allMatch(value -> value <= maxCapacity);
    }

    private void setForbiddenChargeIntervals(Set<Integer> forbiddenChargeIntervals) {
        this.forbiddenChargeIntervals = forbiddenChargeIntervals;
    }

    private void setForbiddenDischargeIntervals(Set<Integer> forbiddenDischargeIntervals) {
        this.forbiddenDischargeIntervals = forbiddenDischargeIntervals;
    }

    /**
     * Two storages are considered equal when their ids are equal
     * @param o Storage object
     * @return if storages are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Storage storage)) return false;
        return getId() == storage.getId();
    }

    /**
     * Two storages are considered equal when their ids are equal
     * @return id value
     */
    @Override
    public int hashCode() {
        return getId();
    }

    /**
     * Storage builder used for setting up all storage constraints.
     */
    @RequiredArgsConstructor
    public static class StorageBuilder {
        private final int id;
        private final String name;
        private final double maxCharge;
        private final double maxDischarge;
        private final double maxCapacity;
        private double initialEnergy;
        private Map<Integer, Double> minChargeConstraints;
        private Map<Integer, Double> maxChargeConstraints;
        private Map<Integer, Double> minDischargeConstraints;
        private Map<Integer, Double> maxDischargeConstraints;
        private Map<Integer, Double> minEnergyConstraints;
        private Map<Integer, Double> maxEnergyConstraints;
        private Set<Integer> forbiddenChargeIntervals;
        private Set<Integer> forbiddenDischargeIntervals;

        public StorageBuilder initialEnergy(double initialEnergy) {
            this.initialEnergy = initialEnergy;
            return this;
        }

        public StorageBuilder minChargeConstraint(int index, double value) {

            if (this.minChargeConstraints == null) {
                this.minChargeConstraints = new HashMap<>();
            }
            this.minChargeConstraints.put(index, value);
            return this;
        }

        public StorageBuilder minChargeConstraints(Map<Integer, Double> minChargeConstraints) {

            this.minChargeConstraints = minChargeConstraints;
            return this;
        }

        public StorageBuilder maxChargeConstraint(int index, double value) {

            if (this.maxChargeConstraints == null) {
                this.maxChargeConstraints = new HashMap<>();
            }
            this.maxChargeConstraints.put(index, value);
            return this;
        }

        public StorageBuilder maxChargeConstraints(Map<Integer, Double> maxChargeConstraints) {

            this.maxChargeConstraints = maxChargeConstraints;
            return this;
        }

        public StorageBuilder maxDischargeConstraint(int index, double value) {

            if (this.maxDischargeConstraints == null) {
                this.maxDischargeConstraints = new HashMap<>();
            }
            this.maxDischargeConstraints.put(index, value);
            return this;
        }

        public StorageBuilder maxDischargeConstraints(Map<Integer, Double> maxDischargeConstraints) {

            this.maxDischargeConstraints = maxDischargeConstraints;
            return this;
        }

        public StorageBuilder minDischargeConstraint(int index, double value) {

            if (this.minDischargeConstraints == null) {
                this.minDischargeConstraints = new HashMap<>();
            }
            this.minDischargeConstraints.put(index, value);
            return this;
        }

        public StorageBuilder minDischargeConstraints(Map<Integer, Double> minDischargeConstraints) {

            this.minDischargeConstraints = minDischargeConstraints;
            return this;
        }


        public StorageBuilder minEnergyConstraint(int index, double value) {

            if (this.minEnergyConstraints == null) {
                this.minEnergyConstraints = new HashMap<>();
            }
            this.minEnergyConstraints.put(index, value);
            return this;
        }

        public StorageBuilder minEnergyConstraints(Map<Integer, Double> minEnergyConstraints) {

            this.minEnergyConstraints = minEnergyConstraints;
            return this;
        }

        public StorageBuilder maxEnergyConstraint(int index, double value) {

            if (this.maxEnergyConstraints == null) {
                this.maxEnergyConstraints = new HashMap<>();
            }
            this.maxEnergyConstraints.put(index, value);
            return this;
        }

        public StorageBuilder maxEnergyConstraints(Map<Integer, Double> maxEnergyConstraints) {

            this.maxEnergyConstraints = maxEnergyConstraints;
            return this;
        }

        public StorageBuilder forbiddenChargeIntervals(Set<Integer> forbiddenChargeIntervals) {

            this.forbiddenChargeIntervals = forbiddenChargeIntervals;
            return this;
        }

        public StorageBuilder forbiddenChargeInterval(int interval) {

            if (this.forbiddenChargeIntervals == null) {
                this.forbiddenChargeIntervals = new HashSet<>();
            }
            this.forbiddenChargeIntervals.add(interval);
            return this;
        }

        public StorageBuilder forbiddenDischargeIntervals(Set<Integer> forbiddenDischargeIntervals) {

            this.forbiddenDischargeIntervals = forbiddenDischargeIntervals;
            return this;
        }

        public StorageBuilder forbiddenDischargeInterval(int interval) {

            if (this.forbiddenDischargeIntervals == null) {
                this.forbiddenDischargeIntervals = new HashSet<>();
            }
            this.forbiddenDischargeIntervals.add(interval);
            return this;
        }

        /**
         * Builds storage using provided data.
         * @return storage with all constraints set up
         * @throws IllegalStateException if provided data is invalid.
         */
        public Storage build() {

            Storage storage = new Storage(this.id, this.name, this.maxCharge, this.maxDischarge, this.maxCapacity);

            if (!storage.setInitialEnergy(this.initialEnergy)) {
                throw new IllegalStateException(
                        String.format("Building storage with id: %s - Invalid initial energy value", this.id));
            }

            if (this.minChargeConstraints != null) {
                if (!storage.setMinChargeConstraints(this.minChargeConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building storage with id: %s - Invalid minimal charge constraints", this.id));
                }
            }
            if (this.maxChargeConstraints != null) {
                if (!storage.setMaxChargeConstraints(this.maxChargeConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building storage with id: %s - Invalid maximal charge constraints", this.id));
                }
            }
            if (this.minDischargeConstraints != null) {
                if (!storage.setMinDischargeConstraints(this.minDischargeConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building storage with id: %s - Invalid minimal discharge constraints", this.id));
                }
            }
            if (this.maxDischargeConstraints != null) {
                if (!storage.setMaxDischargeConstraints(this.maxDischargeConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building storage with id: %s - Invalid maximal discharge constraints", this.id));
                }
            }
            if (this.minEnergyConstraints != null) {
                if (!storage.setMinEnergyConstraints(this.minEnergyConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building storage with id: %s - Invalid minimal energy constraints", this.id));
                }
            }
            if (this.maxEnergyConstraints != null) {
                if (!storage.setMaxEnergyConstraints(this.maxEnergyConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building storage with id: %s - Invalid maximal energy constraints", this.id));
                }
            }
            if (this.forbiddenChargeIntervals != null) {
                storage.setForbiddenChargeIntervals(this.forbiddenChargeIntervals);
            }
            if (this.forbiddenDischargeIntervals != null) {
                storage.setForbiddenDischargeIntervals(this.forbiddenDischargeIntervals);
            }

            return storage;
        }
    }
}
