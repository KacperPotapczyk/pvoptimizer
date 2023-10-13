package com.github.kacperpotapczyk.pvoptimizer.service;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractDirection;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemandResult;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.Storage;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageMode;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageModeProfile;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.SumConstraint;
import com.github.kacperpotapczyk.pvoptimizer.service.index.ContractVariablesStartIndexes;
import com.github.kacperpotapczyk.pvoptimizer.service.index.MovableDemandVariablesStartIndexes;
import com.github.kacperpotapczyk.pvoptimizer.service.index.StorageVariablesStartIndexes;
import com.github.kacperpotapczyk.pvoptimizer.solver.LpSolveSolver;
import com.github.kacperpotapczyk.pvoptimizer.solver.Solver;
import com.github.kacperpotapczyk.pvoptimizer.solver.enums.SolutionStatus;
import com.github.kacperpotapczyk.pvoptimizer.solver.exceptions.SolverException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Service responsible for creating model based on received {@link Task task} data
 * and then invoking {@link com.github.kacperpotapczyk.pvoptimizer.solver.Solver solver} to find optimal solution.
 * Optimization goal is to find optimal electric energy purchase, sell and storage strategy to minimize operation costs
 * while fulfilling fixed PV generation and household electricity demand profiles.
 */
@Slf4j
@Service
public class OptimizerImpl implements Optimizer {

    @Value("${optimizer.maxAllowedTimeOut}")
    private Long maxAllowedTimeOut;

    @Value("${optimizer.numericalZero}")
    private double numericalZero;

    @Override
    public Result solve(Task task) {

        Result.ResultBuilder resultBuilder = Result.builder();
        Long taskId = task.getId();
        resultBuilder.id(taskId);
        log.debug("Building model for task={}", taskId);
        try {
            Solver solver = new LpSolveSolver();
            solver.setTimeOut(Math.min(task.getTimeoutSeconds(), maxAllowedTimeOut));
            solver.setRelativeGap(Math.max(numericalZero, task.getRelativeGap()));

            List<Integer> intervals = IntStream.iterate(0, i -> i + 1)
                    .limit(task.optimizationHorizonLength())
                    .boxed()
                    .toList();

            Map<Long, ContractVariablesStartIndexes> contractStartIndexes = new HashMap<>(task.getContracts().size());
            Map<Long, StorageVariablesStartIndexes> storageStartIndexes = new HashMap<>(task.getStorages().size());
            Map<Long, Set<MovableDemandVariablesStartIndexes>> movableDemandVariablesIndexes = new HashMap<>(task.getMovableDemands().size());

            assignContractsVariables(task, solver, contractStartIndexes);
            assignStoragesVariables(task, solver, storageStartIndexes);
            assignMovableDemandVariables(task, solver, movableDemandVariablesIndexes);

            setUpPowerBalance(task, solver, intervals, contractStartIndexes, storageStartIndexes, movableDemandVariablesIndexes);

            setUpContractsConstraints(task, solver, intervals, contractStartIndexes);
            setUpStoragesConstraints(task, solver, intervals, storageStartIndexes);
            setUpMovableDemandConstraints(task, solver, movableDemandVariablesIndexes);

            setUpObjectiveFunction(task, solver, intervals, contractStartIndexes);

            log.info("Solving task={}", taskId);
            SolutionStatus solutionStatus = solver.solve();

            if (solutionStatus == SolutionStatus.OPTIMAL || solutionStatus == SolutionStatus.SUBOPTIMAL) {
                log.info("Solution found for task={}", taskId);
                log.debug("Result details task={}, status={}, objectiveFunction={}, elapsedTime={}, relativeGap={}",
                        taskId,
                        solutionStatus,
                        solver.getObjectiveValue(),
                        solver.getSolutionElapsedTime(),
                        solver.getSolutionRelativeGap()
                );
                getResult(task, solver, resultBuilder, contractStartIndexes, storageStartIndexes, movableDemandVariablesIndexes);
            }
            else {
                log.info("Solution could not be found for task={}", taskId);
                resultBuilder.optimizationStatus(OptimizationStatus.SOLUTION_NOT_FOUND)
                        .errorMessage("Solution could not be found.");
            }
            solver.free();
        }
        catch (RuntimeException | SolverException exception) {
            log.error("Exception at solving task={}, details={}", taskId, exception.getMessage());
            resultBuilder.optimizationStatus(OptimizationStatus.SOLUTION_NOT_FOUND)
                    .errorMessage(exception.getMessage());
        }

        return resultBuilder.build();
    }

    private void assignContractsVariables(
            Task task,
            Solver solver,
            Map<Long, ContractVariablesStartIndexes> contractStartIndexes) throws SolverException {

        for (Contract contract : task.getContracts()) {
            int contractLength = Math.min(contract.getContractLength(), task.optimizationHorizonLength() - contract.getStartInterval());

            contractStartIndexes.put(
                    contract.getId(),
                    new ContractVariablesStartIndexes(
                            solver.addVariables(contractLength),
                            contractLength
                    )
            );
        }
    }

    private void assignStoragesVariables(
            Task task,
            Solver solver,
            Map<Long, StorageVariablesStartIndexes> storageStartIndex) throws SolverException {

        int taskLength = task.getIntervals().getLength();

        for (Storage storage : task.getStorages()) {
            storageStartIndex.put(
                    storage.getId(),
                    new StorageVariablesStartIndexes(
                            solver.addVariables(taskLength),
                            solver.addVariables(taskLength),
                            solver.addBinaryVariables(taskLength),
                            solver.addVariables(taskLength)
                    )
            );
        }
    }

    private void assignMovableDemandVariables(
            Task task,
            Solver solver,
            Map<Long, Set<MovableDemandVariablesStartIndexes>> movableDemandStartIndexes) throws SolverException {

        int optimizationHorizonLength = task.optimizationHorizonLength();

        for (MovableDemand movableDemand : task.getMovableDemands()) {

            int demandLength = movableDemand.profile().size();
            Set<Integer> movableDemandStartIntervals = movableDemand.startIntervals();

            Map<Integer, Integer> variantLength = new HashMap<>(movableDemandStartIntervals.size());
            Set<MovableDemandVariablesStartIndexes> movableDemandVariantVariablesStartIndexes = new HashSet<>(movableDemandStartIntervals.size());

            for (int startInterval : movableDemandStartIntervals) {

                // check if variant exceeds optimization horizon intervals
                int adjustedLength = Math.min(demandLength, optimizationHorizonLength - startInterval);
                variantLength.put(startInterval, adjustedLength);

                int firstVariable = solver.addVariables(adjustedLength);

                movableDemandVariantVariablesStartIndexes.add(
                        new MovableDemandVariablesStartIndexes(
                                startInterval,
                                solver.addBinaryVariables(1),
                                firstVariable,
                                variantLength.get(startInterval)
                        )
                );
            }

            movableDemandStartIndexes.put(
                    movableDemand.id(),
                    movableDemandVariantVariablesStartIndexes
            );
        }
    }

    private void setUpPowerBalance(
            Task task,
            Solver solver,
            List<Integer> intervals,
            Map<Long, ContractVariablesStartIndexes> contractStartIndexes,
            Map<Long, StorageVariablesStartIndexes> storageStartIndexes,
            Map<Long, Set<MovableDemandVariablesStartIndexes>> movableDemandVariablesIndexes) throws SolverException {

        List<Double> powerBalance = new ArrayList<>(task.optimizationHorizonLength());
        intervals.forEach(interval ->
                powerBalance.add(
                        -1.0 * task.getProduction().getProfile().getValueForInterval(interval).orElse(0.0)
                                + task.getDemand().getProfile().getValueForInterval(interval).orElse(0.0)
                ));

        Profile rhs = new Profile(powerBalance);

        for (int interval : intervals) {

            Map<Integer, Double> contractBalanceWeights = task.getContracts().stream()
                    .filter(contract -> contract.isContractActiveAtInterval(interval))
                    .collect(Collectors.toMap(
                            contract -> contractStartIndexes.get(contract.getId()).power() + interval - contract.getStartInterval(),
                            contract -> contract.getContractDirection() == ContractDirection.PURCHASE ? 1.0 : -1.0
                    ));
            Map<Integer, Double> balanceWeights = new HashMap<>(contractBalanceWeights);

            Map<Integer, Double> storageChargeBalanceWeights = task.getStorages().stream()
                    .collect(Collectors.toMap(
                            storage -> storageStartIndexes.get(storage.getId()).charge() + interval,
                            value -> -1.0
                    ));
            balanceWeights.putAll(storageChargeBalanceWeights);

            Map<Integer, Double> storageDischargeBalanceWeights = task.getStorages().stream()
                    .collect(Collectors.toMap(
                            storage -> storageStartIndexes.get(storage.getId()).discharge() + interval,
                            value -> 1.0
                    ));
            balanceWeights.putAll(storageDischargeBalanceWeights);

            for (MovableDemand movableDemand : task.getMovableDemands()) {

                Set<MovableDemandVariablesStartIndexes> movableDemandVariablesStartData = movableDemandVariablesIndexes.get(movableDemand.id());

                Map<Integer, Double> movableDemandWeights = new HashMap<>();
                for (MovableDemandVariablesStartIndexes data : movableDemandVariablesStartData) {

                    int startInterval = data.startInterval();
                    if (interval >= startInterval && interval < startInterval + data.length()) {
                        movableDemandWeights.put(
                                data.power() + interval - startInterval,
                                -1.0
                        );
                    }
                    balanceWeights.putAll(movableDemandWeights);
                }
            }
            solver.addEqWeightedSumConstraint(balanceWeights, rhs.getValueForInterval(interval).orElseThrow());
        }
    }

    private void setUpContractsConstraints(
            Task task,
            Solver solver,
            List<Integer> intervals,
            Map<Long, ContractVariablesStartIndexes> contractStartIndexes) throws SolverException {

        Profile taskIntervals = task.getIntervals();

        for (Contract contract : task.getContracts()) {

            int powerStartIndex = contractStartIndexes.get(contract.getId()).power();

            setUpContractPowerConstraints(solver, intervals, contract, powerStartIndex);
            setUpContractEnergyConstraints(solver, intervals, contract, powerStartIndex, taskIntervals);
        }
    }

    private void setUpContractPowerConstraints(
            Solver solver,
            List<Integer> intervals,
            Contract contract,
            int powerStartIndex) throws SolverException {

        if (contract.getMinPowerConstraints() != null) {
            solver.addLowerBounds(contract.getMinPowerConstraints().entrySet().stream()
                    .filter(entry -> intervals.contains(entry.getKey()))
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + powerStartIndex,
                            Map.Entry::getValue
                    ))
            );
        }

        if (contract.getMaxPowerConstraints() != null) {
            solver.addUpperBounds(contract.getMaxPowerConstraints().entrySet().stream()
                    .filter(entry -> intervals.contains(entry.getKey()))
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + powerStartIndex,
                            Map.Entry::getValue
                    ))
            );
        }
    }

    private void setUpContractEnergyConstraints(
            Solver solver,
            List<Integer> intervals,
            Contract contract,
            int powerStartIndex,
            Profile taskIntervals) throws SolverException {

        if (contract.getMinEnergyConstraints() != null) {
            for (SumConstraint minEnergyConstraint : contract.getMinEnergyConstraints()) {

                solver.addGeqWeightedSumConstraint(
                        intervals.stream()
                                .filter(interval -> interval>=minEnergyConstraint.startInterval() && interval<=minEnergyConstraint.endInterval())
                                .collect(Collectors.toMap(
                                        interval -> powerStartIndex + interval,
                                        interval -> taskIntervals.getValueForInterval(interval).orElseThrow()
                                )),
                        minEnergyConstraint.sum()
                );
            }
        }

        if (contract.getMaxEnergyConstraints() != null) {
            for (SumConstraint maxEnergyConstraint : contract.getMaxEnergyConstraints()) {

                solver.addLeqWeightedSumConstraint(
                        intervals.stream()
                                .filter(interval -> interval>=maxEnergyConstraint.startInterval() && interval<=maxEnergyConstraint.endInterval())
                                .collect(Collectors.toMap(
                                        interval -> powerStartIndex + interval,
                                        interval -> taskIntervals.getValueForInterval(interval).orElseThrow()
                                )),
                        maxEnergyConstraint.sum()
                );
            }
        }
    }

    private void setUpStoragesConstraints(
            Task task,
            Solver solver,
            List<Integer> intervals,
            Map<Long, StorageVariablesStartIndexes> storageStartIndexes) throws SolverException {

        for (Storage storage : task.getStorages()) {

            long storageId = storage.getId();
            int energyStartIndex = storageStartIndexes.get(storageId).energy();
            int chargeStartIndex = storageStartIndexes.get(storageId).charge();
            int chargeIndicatorStartIndex = storageStartIndexes.get(storageId).chargeIndicator();
            int dischargeStartIndex = storageStartIndexes.get(storageId).discharge();
            double storageBigM = Math.max(storage.getMaxCharge(), storage.getMaxDischarge()) * 1e2;

            setUpStorageEnergyBalance(solver, intervals, task.getIntervals().getValues(), storage, energyStartIndex, chargeStartIndex, dischargeStartIndex);
            setUpStorageModeIndicators(solver, intervals, chargeStartIndex, chargeIndicatorStartIndex, dischargeStartIndex, storageBigM);

            setUpStorageChargeConstraints(solver, intervals, storage, chargeStartIndex);
            setUpStorageDischargeConstraints(solver, intervals, storage, dischargeStartIndex);
            setUpStorageEnergyConstraints(solver, intervals, storage, energyStartIndex);
            setUpStorageForbiddenStates(solver, storage, chargeStartIndex, dischargeStartIndex);
        }
    }

    private void setUpStorageEnergyBalance(
            Solver solver,
            List<Integer> intervals,
            List<Double> intervalsDuration,
            Storage storage,
            int energyStartIndex,
            int chargeStartIndex,
            int dischargeStartIndex) throws SolverException {

        // first interval energy balance
        Map<Integer, Double> firstIntervalBalance = new HashMap<>(3);
        firstIntervalBalance.put(energyStartIndex, -1.0);
        firstIntervalBalance.put(chargeStartIndex, 1.0 * intervalsDuration.get(0));
        firstIntervalBalance.put(dischargeStartIndex, -1.0 * intervalsDuration.get(0));
        solver.addEqWeightedSumConstraint(firstIntervalBalance,-1.0 * storage.getInitialEnergy());

        // energy balance for the rest of intervals
        Map<Integer, Double> intervalBalance = new HashMap<>(4);
        for (int interval : intervals.stream().skip(1L).toList()) {

            intervalBalance.clear();
            intervalBalance.put(energyStartIndex + interval - 1, 1.0);
            intervalBalance.put(energyStartIndex + interval, -1.0);
            intervalBalance.put(chargeStartIndex + interval, 1.0 * intervalsDuration.get(interval));
            intervalBalance.put(dischargeStartIndex + interval, -1.0 * intervalsDuration.get(interval));
            solver.addEqWeightedSumConstraint(intervalBalance,0.0);
        }
    }

    private void setUpStorageModeIndicators(
            Solver solver,
            List<Integer> intervals,
            int chargeStartIndex,
            int chargeIndicatorStartIndex,
            int dischargeStartIndex,
            double storageBigM) throws SolverException {

        for (int interval : intervals) {
            Map<Integer, Double> chargeIndexedValues = new HashMap<>(2);
            chargeIndexedValues.put(chargeStartIndex + interval, 1.0);
            chargeIndexedValues.put(chargeIndicatorStartIndex + interval, -1.0*storageBigM);
            solver.addLeqWeightedSumConstraint(
                    chargeIndexedValues,
                    0.0
            );

            Map<Integer, Double> dischargeIndexedValues = new HashMap<>(2);
            dischargeIndexedValues.put(dischargeStartIndex + interval, 1.0);
            dischargeIndexedValues.put(chargeIndicatorStartIndex + interval, storageBigM);
            solver.addLeqWeightedSumConstraint(
                    dischargeIndexedValues,
                    storageBigM
            );
        }
    }

    private void setUpStorageChargeConstraints(
            Solver solver,
            List<Integer> intervals,
            Storage storage,
            int chargeStartIndex) throws SolverException {

        if (storage.getMinChargeConstraints() != null) {
            solver.addLowerBounds(storage.getMinChargeConstraints().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + chargeStartIndex,
                            Map.Entry::getValue
                    ))
            );
        }
        if (storage.getMaxChargeConstraints() != null) {
            solver.addUpperBounds(storage.getMaxChargeConstraints().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + chargeStartIndex,
                            Map.Entry::getValue
                    ))
            );
            solver.addUpperBounds(intervals.stream()
                    .filter(interval -> !storage.getMaxChargeConstraints().containsKey(interval))
                    .collect(Collectors.toMap(
                            interval -> interval + chargeStartIndex,
                            interval -> storage.getMaxCharge()
                    ))
            );
        }
        else {
            solver.addUpperBounds(intervals.stream()
                    .collect(Collectors.toMap(
                            interval -> interval + chargeStartIndex,
                            interval -> storage.getMaxCharge()
                    ))
            );
        }
    }

    private void setUpStorageDischargeConstraints(
            Solver solver,
            List<Integer> intervals,
            Storage storage,
            int dischargeStartIndex) throws SolverException {

        if (storage.getMinDischargeConstraints() != null) {
            solver.addLowerBounds(storage.getMinDischargeConstraints().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + dischargeStartIndex,
                            Map.Entry::getValue
                    ))
            );
        }
        if (storage.getMaxDischargeConstraints() != null) {
            solver.addUpperBounds(storage.getMaxDischargeConstraints().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + dischargeStartIndex,
                            Map.Entry::getValue
                    ))
            );
            solver.addUpperBounds(intervals.stream()
                    .filter(interval -> !storage.getMaxDischargeConstraints().containsKey(interval))
                    .collect(Collectors.toMap(
                            interval -> interval + dischargeStartIndex,
                            interval -> storage.getMaxDischarge()
                    ))
            );
        }
        else {
            solver.addUpperBounds(intervals.stream()
                    .collect(Collectors.toMap(
                            interval -> interval + dischargeStartIndex,
                            interval -> storage.getMaxDischarge()
                    ))
            );
        }
    }

    private void setUpStorageEnergyConstraints(
            Solver solver,
            List<Integer> intervals,
            Storage storage,
            int energyStartIndex) throws SolverException {
        
        if (storage.getMinEnergyConstraints() != null) {
            solver.addLowerBounds(storage.getMinEnergyConstraints().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + energyStartIndex,
                            Map.Entry::getValue
                    ))
            );
        }
        if (storage.getMaxEnergyConstraints() != null) {
            solver.addUpperBounds(storage.getMaxEnergyConstraints().entrySet().stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey() + energyStartIndex,
                            Map.Entry::getValue
                    ))
            );
            solver.addUpperBounds(intervals.stream()
                    .filter(interval -> !storage.getMaxEnergyConstraints().containsKey(interval))
                    .collect(Collectors.toMap(
                            interval -> interval + energyStartIndex,
                            interval -> storage.getMaxCapacity()
                    ))
            );
        }
        else {
            solver.addUpperBounds(intervals.stream()
                    .collect(Collectors.toMap(
                            interval -> interval + energyStartIndex,
                            interval -> storage.getMaxCapacity()
                    ))
            );
        }
    }

    private void setUpStorageForbiddenStates(
            Solver solver,
            Storage storage,
            int chargeStartIndex,
            int dischargeStartIndex) throws SolverException {

        if (storage.getForbiddenChargeIntervals() != null) {
            solver.fixVariables(storage.getForbiddenChargeIntervals().stream()
                    .collect(Collectors.toMap(
                            interval -> interval + chargeStartIndex,
                            interval -> 0.0
                    ))
            );
        }
        if (storage.getForbiddenDischargeIntervals() != null) {
            solver.fixVariables(storage.getForbiddenDischargeIntervals().stream()
                    .collect(Collectors.toMap(
                            interval -> interval + dischargeStartIndex,
                            interval -> 0.0
                    ))
            );
        }
    }

    private void setUpMovableDemandConstraints(
            Task task,
            Solver solver,
            Map<Long, Set<MovableDemandVariablesStartIndexes>> movableDemandVariablesIndexes) throws SolverException {

        for (MovableDemand movableDemand : task.getMovableDemands()) {

            Set<MovableDemandVariablesStartIndexes> dataSet = movableDemandVariablesIndexes.get(movableDemand.id());

            // connect indicators with variables using equal constraint
            for (MovableDemandVariablesStartIndexes data : dataSet) {

                Map<Integer, Double> intervalBalance = new HashMap<>(2);
                for (int index : IntStream.range(0, data.length()).boxed().toList()) {

                    intervalBalance.clear();
                    intervalBalance.put(data.variantIndicator(), -1.0*movableDemand.profile().get(index));
                    intervalBalance.put(data.power() + index, 1.0);

                    solver.addEqWeightedSumConstraint(intervalBalance, 0.0);
                }
            }

            // sum of all indicators must be 1
            solver.addEqSumConstraint(
                    dataSet.stream()
                            .map(MovableDemandVariablesStartIndexes::variantIndicator)
                            .collect(Collectors.toSet()),
                    1.0
            );
        }
    }

    private void setUpObjectiveFunction(
            Task task,
            Solver solver,
            List<Integer> intervals,
            Map<Long, ContractVariablesStartIndexes> contractStartIndexes) throws SolverException {

        Map<Integer, Double> costCoefficients = new HashMap<>();
        Profile taskIntervals = task.getIntervals();

        for (Contract contract : task.getContracts()) {

            int powerStartIndex = contractStartIndexes.get(contract.getId()).power();
            for (int interval : intervals) {

                if (contract.isContractActiveAtInterval(interval)) {

                    if (contract.getContractDirection() == ContractDirection.PURCHASE) {
                        costCoefficients.put(
                                powerStartIndex + interval - contract.getStartInterval(),
                                contract.getUnitPrice().getValueForInterval(interval).orElseThrow() *
                                        taskIntervals.getValueForInterval(interval).orElse(0.0)
                        );
                    }
                    else {
                        costCoefficients.put(
                                powerStartIndex + interval - contract.getStartInterval(),
                                -1.0 * contract.getUnitPrice().getValueForInterval(interval).orElseThrow() *
                                    taskIntervals.getValueForInterval(interval).orElse(0.0)
                        );
                    }
                }
            }
        }
        solver.setObjectiveFunction(costCoefficients);
    }

    private void getResult(
            Task task,
            Solver solver,
            Result.ResultBuilder resultBuilder,
            Map<Long, ContractVariablesStartIndexes> contractStartIndexes,
            Map<Long, StorageVariablesStartIndexes> storageStartIndexes,
            Map<Long, Set<MovableDemandVariablesStartIndexes>> movableDemandVariablesData) throws SolverException {

        resultBuilder
                .optimizationStatus(OptimizationStatus.SOLUTION_FOUND)
                .errorMessage("")
                .relativeGap(solver.getSolutionRelativeGap())
                .elapsedTime(solver.getSolutionElapsedTime())
                .objectiveFunctionValue(solver.getObjectiveValue());

        Map<Integer, Double> variableResults = solver.getSolution();

        getContractsResults(task, resultBuilder, contractStartIndexes, variableResults);
        getStoragesResults(task, resultBuilder, storageStartIndexes, variableResults);
        getMovableDemandResults(task, resultBuilder, movableDemandVariablesData, variableResults);
    }

    private void getContractsResults(
            Task task,
            Result.ResultBuilder resultBuilder,
            Map<Long, ContractVariablesStartIndexes> contractStartIndexes,
            Map<Integer, Double> variableResults) {

        for (Contract contract : task.getContracts()) {

            int contractPowerVariableStart = contractStartIndexes.get(contract.getId()).power();
            int contractLength = contractStartIndexes.get(contract.getId()).length();

            List<Double> power = variableResults.entrySet().stream()
                    .filter(entry -> entry.getKey() >= contractPowerVariableStart &&
                            entry.getKey() < contractPowerVariableStart + contractLength)
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .toList();

            List<Double> contractIntervalsDuration = IntStream.range(contract.getStartInterval(), contract.getStartInterval() + contractLength)
                    .boxed()
                    .map(
                            interval -> task.getIntervals().getValueForInterval(interval).orElseThrow()
                    )
                    .toList();

            List<Double> energy = new ArrayList<>(contractLength);
            for (int i=0; i<power.size(); i++) {
                energy.add(power.get(i) * contractIntervalsDuration.get(i));
            }

            List<Double> cost = contractCostForEnergy(contract, energy);

            resultBuilder.contractResult(new ContractResult(
                    contract.getId(),
                    contract.getName(),
                    new Profile(contract.getStartInterval(), power),
                    new Profile(contract.getStartInterval(), energy),
                    new Profile(contract.getStartInterval(), cost)
            ));
        }
    }

    private void getStoragesResults(
            Task task,
            Result.ResultBuilder resultBuilder,
            Map<Long, StorageVariablesStartIndexes> storageStartIndexes,
            Map<Integer, Double> variableResults) {

        for (Storage storage : task.getStorages()) {

            int energyStartIndex = storageStartIndexes.get(storage.getId()).energy();
            int chargeStartIndex = storageStartIndexes.get(storage.getId()).charge();
            int dischargeStartIndex = storageStartIndexes.get(storage.getId()).discharge();

            Profile charge = new Profile(
                    variableResults.entrySet().stream()
                            .filter(entry -> entry.getKey() >= chargeStartIndex &&
                                    entry.getKey() < chargeStartIndex + task.optimizationHorizonLength())
                            .sorted(Map.Entry.comparingByKey())
                            .map(Map.Entry::getValue)
                            .toList()
            );

            Profile discharge = new Profile(
                    variableResults.entrySet().stream()
                            .filter(entry -> entry.getKey() >= dischargeStartIndex &&
                                    entry.getKey() < dischargeStartIndex + task.optimizationHorizonLength())
                            .sorted(Map.Entry.comparingByKey())
                            .map(Map.Entry::getValue)
                            .toList()
            );

            Profile energy = new Profile(
                    variableResults.entrySet().stream()
                            .filter(entry -> entry.getKey() >= energyStartIndex &&
                                    entry.getKey() < energyStartIndex + task.optimizationHorizonLength())
                            .sorted(Map.Entry.comparingByKey())
                            .map(Map.Entry::getValue)
                            .toList()
            );

            List<Integer> chargeIndicators = variableResults.entrySet().stream()
                    .filter(entry -> entry.getKey() >= chargeStartIndex &&
                            entry.getKey() < chargeStartIndex + task.optimizationHorizonLength())
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> {
                        if (entry.getValue() > numericalZero) {
                            return 1;
                        }
                        return 0;
                    })
                    .toList();

            List<Integer> dischargeIndicators = variableResults.entrySet().stream()
                    .filter(entry -> entry.getKey() >= dischargeStartIndex &&
                            entry.getKey() < dischargeStartIndex + task.optimizationHorizonLength())
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> {
                        if (entry.getValue() > numericalZero) {
                            return -1;
                        }
                        return 0;
                    })
                    .toList();

            StorageModeProfile storageMode = new StorageModeProfile(
                    IntStream.range(0, task.optimizationHorizonLength())
                            .map(i -> chargeIndicators.get(i) + dischargeIndicators.get(i))
                            .mapToObj(modeInteger -> {
                                if (modeInteger == 1) {
                                    return StorageMode.CHARGING;
                                }
                                else if (modeInteger == -1) {
                                    return StorageMode.DISCHARGING;
                                }
                                else {
                                    return StorageMode.DISABLED;
                                }
                            })
                            .toList()
            );

            resultBuilder.storageResult(new StorageResult(
                    storage.getId(),
                    storage.getName(),
                    charge,
                    discharge,
                    energy,
                    storageMode
            ));
        }
    }

    private void getMovableDemandResults(
            Task task,
            Result.ResultBuilder resultBuilder,
            Map<Long, Set<MovableDemandVariablesStartIndexes>> movableDemandVariablesData,
            Map<Integer, Double> variableResults) {

        for (MovableDemand movableDemand : task.getMovableDemands()) {

            Map<Integer, Integer> indexIntervalMap = movableDemandVariablesData.get(movableDemand.id()).stream()
                    .collect(Collectors.toMap(
                            MovableDemandVariablesStartIndexes::variantIndicator,
                            MovableDemandVariablesStartIndexes::startInterval
                    ));

            int interval = variableResults.entrySet().stream()
                    .filter(entry -> indexIntervalMap.containsKey(entry.getKey()))
                    .filter(entry -> entry.getValue() == 1)
                    .map(entry -> indexIntervalMap.get(entry.getKey()))
                    .findFirst().orElse(-1);

            resultBuilder.movableDemandResult(new MovableDemandResult(
                    movableDemand.id(),
                    movableDemand.name(),
                    interval
            ));
        }
    }

    private List<Double> contractCostForEnergy(Contract contract, List<Double> energy) {

        List<Double> cost = new ArrayList<>(energy.size());
        for (int i=0; i<energy.size(); i++) {
            cost.add(contract.getUnitPrice().getValueForIndex(i).orElseThrow() * energy.get(i));
        }

        return cost;
    }
}
