package com.github.kacperpotapczyk.pvoptimizer.model;

import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemandResult;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

@Builder
@Getter
public class Result {

    private final OptimizationStatus optimizationStatus;
    private double objectiveFunctionValue;
    private double relativeGap;
    private double elapsedTime;
    private String errorMessage;
    @Singular
    private List<ContractResult> contractResults;
    @Singular
    private List<StorageResult> storageResults;
    @Singular
    private List<MovableDemandResult> movableDemandResults;
}
