package com.github.kacperpotapczyk.pvoptimizer.model;

import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemandResult;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

/**
 * Structure containing results of given {@link Task task} obtained from {@link com.github.kacperpotapczyk.pvoptimizer.service.Optimizer optimizer}.
 */
@Builder
@Getter
public class Result {

    /**
     * Optimization status.
     */
    private final OptimizationStatus optimizationStatus;
    /**
     * Objective function value.
     */
    private double objectiveFunctionValue;
    /**
     * Relative gap between relaxed solution and returned integer solution.
     */
    private double relativeGap;
    /**
     * Optimization elapsed time.
     */
    private double elapsedTime;
    /**
     * Error messages returned by optimizer.
     */
    private String errorMessage;
    /**
     * Results of contracts utilization.
     */
    @Singular
    private List<ContractResult> contractResults;
    /**
     * Results of electric energy storages
     */
    @Singular
    private List<StorageResult> storageResults;
    /**
     * Results of movable demands
     */
    @Singular
    private List<MovableDemandResult> movableDemandResults;
}
