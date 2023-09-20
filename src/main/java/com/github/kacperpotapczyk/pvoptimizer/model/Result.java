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

    final OptimizationStatus optimizationStatus;
    double objectiveFunctionValue;
    @Singular
    List<ContractResult> contractResults;
    @Singular
    List<StorageResult> storageResults;
    @Singular
    List<MovableDemandResult> movableDemandResults;
}
