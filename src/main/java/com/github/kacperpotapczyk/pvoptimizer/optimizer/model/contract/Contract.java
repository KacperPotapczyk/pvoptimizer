package com.github.kacperpotapczyk.pvoptimizer.optimizer.model.contract;

import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.utils.Profile;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.utils.SumConstraint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * Contract definition required for optimization model. Contract can be available on continuous subset of task intervals.
 */
@Getter
@RequiredArgsConstructor
public class Contract {

    /**
     * Contract id
     */
    private final long id;
    /**
     * Contract name
     */
    private final String name;
    /**
     * Energy unit price for intervals at which contract is available
     */
    private final Profile unitPrice;
    /**
     * Purchase or sell contract direction
     */
    private final ContractDirection contractDirection;
    /**
     * Interval and interval minimal power value constraint pairs
     */
    private Map<Integer, Double> minPowerConstraints;
    /**
     * Interval and interval maximal power value constraint pairs
     */
    private Map<Integer, Double> maxPowerConstraints;
    /**
     * List of constraints on minimal energy over continuous set of intervals
     */
    private List<SumConstraint> minEnergyConstraints;
    /**
     * List of constraints on maximal energy over continuous set of intervals
     */
    private List<SumConstraint> maxEnergyConstraints;

    /**
     * Contract builder with required fields
     * @param id contract id
     * @param name contract name
     * @param unitPrice energy unit price for intervals at which contract is available
     * @param contractDirection Purchase or sell contract direction
     * @return contract builder
     */
    public static ContractBuilder builder(long id, String name, Profile unitPrice, ContractDirection contractDirection) {
        return new ContractBuilder(id, name, unitPrice, contractDirection);
    }

    /**
     * Returns first interval at which contract is available.
     * @return first interval at which contract is available
     */
    public int getStartInterval() {
        return unitPrice.getStartInterval();
    }

    /**
     * Returns last interval at which contract is available.
     * @return last interval at which contract is available
     */
    public int getLastInterval() {
        return getStartInterval() + unitPrice.getLength();
    }

    /**
     * Returns contract length in intervals.
     * @return contract length in intervals
     */
    public int getContractLength() {
        return unitPrice.getLength();
    }

    /**
     * Check if contract is available at given interval
     * @param interval interval to check
     * @return if contract is available at given interval
     */
    public boolean isContractActiveAtInterval(int interval) {
        return interval >= this.getStartInterval() && interval < this.getLastInterval();
    }

    /**
     * Returns maximal contract power at given interval.
     * @param interval interval
     * @return if contract is available then maximal contract power
     */
    public Optional<Double> getMaxPowerConstraintForInterval(int interval) {

        if (maxPowerConstraints.containsKey(interval)) {
            return Optional.of(maxPowerConstraints.get(interval));
        }
        else {
            return Optional.empty();
        }
    }

    /**
     * Returns minimal contract power at given interval.
     * @param interval interval
     * @return if contract is available then minimal contract power
     */
    public Optional<Double> getMinPowerConstraintForInterval(int interval) {

        if (minPowerConstraints.containsKey(interval)) {
            return Optional.of(minPowerConstraints.get(interval));
        }
        else {
            return Optional.empty();
        }
    }

    private boolean setMinPowerConstraints(Map<Integer, Double> minPowerConstraints) {
        if (checkPowerConstraintsIntervalRange(minPowerConstraints)) {
            this.minPowerConstraints = minPowerConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMaxPowerConstraints(Map<Integer, Double> maxPowerConstraints) {
        if (checkPowerConstraintsIntervalRange(maxPowerConstraints)) {
            this.maxPowerConstraints = maxPowerConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMinEnergyConstraints(List<SumConstraint> minVolumeConstraints) {
        if (checkEnergyConstraintsIntervalRange(minVolumeConstraints)) {
            this.minEnergyConstraints = minVolumeConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private boolean setMaxEnergyConstraints(List<SumConstraint> maxVolumeConstraints) {
        if (checkEnergyConstraintsIntervalRange(maxVolumeConstraints)) {
            this.maxEnergyConstraints = maxVolumeConstraints;
            return true;
        }
        else {
            return false;
        }
    }

    private Boolean checkPowerConstraintsIntervalRange(Map<Integer, Double> powerConstraints) {
        return powerConstraints.keySet().stream()
                .allMatch(this::isContractActiveAtInterval);
    }

    private Boolean checkEnergyConstraintsIntervalRange(List<SumConstraint> energyConstraints) {
        return energyConstraints.stream()
                .allMatch(constraint -> isContractActiveAtInterval(constraint.startInterval()) && isContractActiveAtInterval(constraint.endInterval()));
    }

    /**
     * Two contracts are considered equal when their ids are equal
     * @param o Contract object
     * @return if storages are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract contract)) return false;

        return getId() == contract.getId();
    }

    /**
     * Two contracts are considered equal when their ids are equal
     *
     * @return id value
     */
    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }

    /**
     * Contract builder used for setting up all storage constraints.
     */
    @RequiredArgsConstructor
    public static class ContractBuilder {

        private final long id;
        private final String name;
        private final Profile unitPrice;
        private final ContractDirection contractDirection;
        private Map<Integer, Double> minPowerConstraints;
        private Map<Integer, Double> maxPowerConstraints;
        private List<SumConstraint> minEnergyConstraints;
        private List<SumConstraint> maxEnergyConstraints;

        public ContractBuilder minPowerConstraints(Map<Integer, Double> minPowerConstraints) {

            this.minPowerConstraints = minPowerConstraints;
            return this;
        }

        public ContractBuilder minPowerConstraint(int interval, double value) {

            if (this.minPowerConstraints == null) {
                minPowerConstraints = new HashMap<>();
            }
            this.minPowerConstraints.put(interval, value);
            return this;
        }

        public ContractBuilder maxPowerConstraints(Map<Integer, Double> maxPowerConstraints) {

            this.maxPowerConstraints = maxPowerConstraints;
            return this;
        }

        public ContractBuilder maxPowerConstraint(int interval, double value) {

            if (this.maxPowerConstraints == null) {
                maxPowerConstraints = new HashMap<>();
            }
            this.maxPowerConstraints.put(interval, value);
            return this;
        }

        public ContractBuilder minEnergyConstraints(List<SumConstraint> minVolumeConstraints) {

            this.minEnergyConstraints = minVolumeConstraints;
            return this;
        }

        public ContractBuilder minEnergyConstraint(SumConstraint minVolumeConstraints) {

            if (this.minEnergyConstraints == null) {
                this.minEnergyConstraints = new ArrayList<>();
            }
            this.minEnergyConstraints.add(minVolumeConstraints);
            return this;
        }

        public ContractBuilder maxEnergyConstraints(List<SumConstraint> maxVolumeConstraints) {

            this.maxEnergyConstraints = maxVolumeConstraints;
            return this;
        }

        public ContractBuilder maxEnergyConstraint(SumConstraint maxVolumeConstraints) {

            if (this.maxEnergyConstraints == null) {
                this.maxEnergyConstraints = new ArrayList<>();
            }
            this.maxEnergyConstraints.add(maxVolumeConstraints);
            return this;
        }

        /**
         * Builds contract using provided data.
         * @return contract with all constraints set up
         * @throws IllegalStateException if provided data is invalid.
         */
        public Contract build() {

            Contract contract = new Contract(this.id, this.name, this.unitPrice, this.contractDirection);

            if (this.minPowerConstraints != null) {
                if (!contract.setMinPowerConstraints(this.minPowerConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building contract with id: %s - Invalid minimal power constraints", this.id)
                    );
                }
            }

            if (this.maxPowerConstraints != null) {
                if (!contract.setMaxPowerConstraints(this.maxPowerConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building contract with id: %s - Invalid maximal power constraints", this.id)
                    );
                }
            }

            if (this.minEnergyConstraints != null) {
                if (!contract.setMinEnergyConstraints(this.minEnergyConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building contract with id: %s - Invalid minimal energy constraints", this.id)
                    );
                }
            }

            if (this.maxEnergyConstraints != null) {
                if (!contract.setMaxEnergyConstraints(this.maxEnergyConstraints)) {
                    throw new IllegalStateException(
                            String.format("Building contract with id: %s - Invalid maximal energy constraints", this.id)
                    );
                }
            }

            return contract;
        }
    }
}
