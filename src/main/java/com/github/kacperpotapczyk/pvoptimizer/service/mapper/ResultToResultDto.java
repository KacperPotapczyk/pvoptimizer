package com.github.kacperpotapczyk.pvoptimizer.service.mapper;

import com.github.kacperpotapczyk.pvoptimizer.dto.*;
import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemandResult;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageMode;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for mapping Result to ResultDto produced by {@link com.github.kacperpotapczyk.pvoptimizer.service.optimizer.Optimizer optimizer}.
 * Implements mapping from Result to ResultDto objects
 */
@Service
public class ResultToResultDto implements Mapper<Result, ResultDto> {

    /**
     * Specific mapping method between Result and ResultDto objects
     * @param result input Result
     * @return output ResultDto
     */
    @Override
    public ResultDto map(Result result) {

        ResultDto.Builder resutlDtoBuilder = ResultDto.newBuilder();
        resutlDtoBuilder
                .setId(result.getId())
                .setOptimizationStatus(optimizationStatusMapper(result.getOptimizationStatus()))
                .setObjectiveFunctionValue(result.getObjectiveFunctionValue())
                .setRelativeGap(result.getRelativeGap())
                .setElapsedTime(result.getElapsedTime())
                .setErrorMessage(result.getErrorMessage());

        mapContractResults(result, resutlDtoBuilder);
        mapStorageResults(result, resutlDtoBuilder);
        mapMovableDemandResults(result, resutlDtoBuilder);

        return resutlDtoBuilder.build();
    }

    private void mapContractResults(Result result, ResultDto.Builder resutlDtoBuilder) {
        List<ContractResultDto> contractResultDtoList = new ArrayList<>(result.getContractResults().size());
        for (ContractResult contractResult : result.getContractResults()) {

            contractResultDtoList.add(new ContractResultDto(
                    contractResult.id(),
                    contractResult.name(),
                    contractResult.power().getValues(),
                    contractResult.energy().getValues(),
                    contractResult.cost().getValues()
            ));
        }
        resutlDtoBuilder.setContractResults(contractResultDtoList);
    }

    private void mapStorageResults(Result result, ResultDto.Builder resutlDtoBuilder) {

        List<StorageResultDto> storageResultDtoList = new ArrayList<>(result.getStorageResults().size());
        for (StorageResult storageResult : result.getStorageResults()) {

            storageResultDtoList.add(new StorageResultDto(
                    storageResult.id(),
                    storageResult.name(),
                    storageResult.charge().getValues(),
                    storageResult.discharge().getValues(),
                    storageResult.energy().getValues(),
                    storageModeListMapper(storageResult.storageMode().getValues())
            ));
        }
        resutlDtoBuilder.setStorageResults(storageResultDtoList);
    }

    private void mapMovableDemandResults(Result result, ResultDto.Builder resutlDtoBuilder) {

        List<MovableDemandResultDto> movableDemandResultDtoList = new ArrayList<>(result.getMovableDemandResults().size());
        for (MovableDemandResult movableDemandResult : result.getMovableDemandResults()) {

            movableDemandResultDtoList.add(new MovableDemandResultDto(
                    movableDemandResult.id(),
                    movableDemandResult.name(),
                    movableDemandResult.startInterval()
            ));
        }
        resutlDtoBuilder.setMovableDemandResults(movableDemandResultDtoList);
    }

    private OptimizationStatusDto optimizationStatusMapper(OptimizationStatus optimizationStatus) {

        if (OptimizationStatus.SOLUTION_FOUND == optimizationStatus) {
            return OptimizationStatusDto.SOLUTION_FOUND;
        }
        else {
            return OptimizationStatusDto.SOLUTION_NOT_FOUND;
        }
    }

    private List<StorageModeDto> storageModeListMapper(List<StorageMode> storageModeList) {

        List<StorageModeDto> storageModeDtoList = new ArrayList<>(storageModeList.size());
        for (StorageMode storageMode : storageModeList) {

            switch (storageMode) {
                case CHARGING -> storageModeDtoList.add(StorageModeDto.CHARGING);
                case DISCHARGING -> storageModeDtoList.add(StorageModeDto.DISCHARGING);
                default -> storageModeDtoList.add(StorageModeDto.DISABLED);
            }
        }
        return storageModeDtoList;
    }
}
