package com.github.kacperpotapczyk.pvoptimizer.model.contract;

import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.SumConstraint;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class Contract {

    private final int id;
    private final String name;
    private final Profile unitPrice;
    private final ContractDirection contractDirection;
    private Map<Integer, Double> minPowerConstraints;
    private Map<Integer, Double> maxPowerConstraints;
    private List<SumConstraint> minEnergyConstraints;
    private List<SumConstraint> maxEnergyConstraints;

    public static ContractBuilder builder(int id, String name, Profile unitPrice, ContractDirection contractDirection) {
        return new ContractBuilder(id, name, unitPrice, contractDirection);
    }

    public int getStartInterval() {
        return unitPrice.getStartInterval();
    }

    public int getLastInterval() {
        return getStartInterval() + unitPrice.getLength();
    }

    public int getContractLength() {
        return unitPrice.getLength();
    }

    public boolean isContractActiveAtInterval(int interval) {
        return interval >= this.getStartInterval() && interval < this.getLastInterval();
    }

    public Optional<Double> getMaxPowerConstraintForInterval(int interval) {

        if (maxPowerConstraints.containsKey(interval)) {
            return Optional.of(maxPowerConstraints.get(interval));
        }
        else {
            return Optional.empty();
        }
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Contract contract)) return false;

        return getId() == contract.getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @RequiredArgsConstructor
    public static class ContractBuilder {

        private final int id;
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
