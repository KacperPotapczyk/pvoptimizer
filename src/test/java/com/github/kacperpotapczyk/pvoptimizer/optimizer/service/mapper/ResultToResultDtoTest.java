package com.github.kacperpotapczyk.pvoptimizer.optimizer.service.mapper;

import com.github.kacperpotapczyk.pvoptimizer.avro.optimizer.result.*;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.sourcesink.MovableDemandResult;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.storage.StorageMode;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.storage.StorageModeProfile;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.utils.OptimizationStatus;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.model.utils.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ResultToResultDtoTest {

    private final Mapper<Result, ResultDto> resultToResultDto;

    @Autowired
    public ResultToResultDtoTest(Mapper<Result, ResultDto> resultToResultDto) {
        this.resultToResultDto = resultToResultDto;
    }

    @Test
    public void testBaseResult() {

        Result.ResultBuilder resultBuilder = getBaseResultBuilder();
        Result result = resultBuilder.build();

        ResultDto resultDto = resultToResultDto.map(result);

        assertNotNull(resultDto);
        assertEquals(1L, resultDto.getId());
        assertEquals(OptimizationStatusDto.SOLUTION_FOUND, resultDto.getOptimizationStatus());
        assertEquals(25.0, resultDto.getObjectiveFunctionValue());
        assertEquals(1e-11, resultDto.getRelativeGap());
        assertEquals(12.0, resultDto.getElapsedTime());
        assertEquals("", resultDto.getErrorMessage());
    }

    @Test
    public void testFailedOptimizationResult() {

        Result.ResultBuilder resultBuilder = Result.builder();
        resultBuilder
                .id(99L)
                .optimizationStatus(OptimizationStatus.SOLUTION_NOT_FOUND)
                .errorMessage("Solution could not be found.");

        ResultDto resultDto = resultToResultDto.map(resultBuilder.build());

        assertNotNull(resultDto);
        assertEquals(99L, resultDto.getId());
        assertEquals(OptimizationStatusDto.SOLUTION_NOT_FOUND, resultDto.getOptimizationStatus());
        assertEquals("Solution could not be found.", resultDto.getErrorMessage());
        assertEquals(0, resultDto.getContractResults().size());
        assertEquals(0, resultDto.getStorageResults().size());
        assertEquals(0, resultDto.getMovableDemandResults().size());
    }

    @Test
    public void testContractResult() {

        Result.ResultBuilder resultBuilder = getBaseResultBuilder();

        List<Double> power = Arrays.asList(1.0, 2.0);
        List<Double> energy = Arrays.asList(0.25, 0.5);
        List<Double> cost = Arrays.asList(4.0, 8.0);

        ContractResult contractResult = new ContractResult(
                33L,
                "Purchase",
                new Profile(power),
                new Profile(energy),
                new Profile(cost)
        );

        resultBuilder.contractResult(contractResult);

        ResultDto resultDto = resultToResultDto.map(resultBuilder.build());
        assertEquals(1, resultDto.getContractResults().size());

        ContractResultDto contractResultDto = resultDto.getContractResults().get(0);
        assertEquals(33L, contractResultDto.getId());
        assertEquals("Purchase", contractResultDto.getName());
        assertEquals(power, contractResultDto.getPower());
        assertEquals(energy, contractResultDto.getEnergy());
        assertEquals(cost, contractResultDto.getCost());
    }

    @Test
    public void testStorageResult() {

        Result.ResultBuilder resultBuilder = getBaseResultBuilder();

        List<Double> charge = Arrays.asList(0.0, 2.0);
        List<Double> discharge = Arrays.asList(1.0, 0.0);
        List<Double> energy = Arrays.asList(2.0, 4.0);
        List<StorageMode> storageMode = Arrays.asList(StorageMode.DISCHARGING, StorageMode.CHARGING);

        StorageResult storageResult = new StorageResult(
                22L,
                "Storage",
                new Profile(charge),
                new Profile(discharge),
                new Profile(energy),
                new StorageModeProfile(storageMode)
        );

        resultBuilder.storageResult(storageResult);

        ResultDto resultDto = resultToResultDto.map(resultBuilder.build());
        assertEquals(1, resultDto.getStorageResults().size());

        StorageResultDto storageResultDto = resultDto.getStorageResults().get(0);
        assertEquals(22L, storageResultDto.getId());
        assertEquals("Storage", storageResultDto.getName());
        assertEquals(charge, storageResultDto.getCharge());
        assertEquals(discharge, storageResultDto.getDischarge());
        assertEquals(energy, storageResultDto.getEnergy());
        assertEquals(StorageModeDto.DISCHARGING, storageResultDto.getStorageMode().get(0));
        assertEquals(StorageModeDto.CHARGING, storageResultDto.getStorageMode().get(1));
    }

    @Test
    public void testMovableDemand() {

        Result.ResultBuilder resultBuilder = getBaseResultBuilder();

        MovableDemandResult movableDemandResult1 = new MovableDemandResult(
                76L,
                "movableDemand1",
                0
        );

        MovableDemandResult movableDemandResult2 = new MovableDemandResult(
                78L,
                "movableDemand2",
                1
        );

        resultBuilder
                .movableDemandResult(movableDemandResult1)
                .movableDemandResult(movableDemandResult2);

        ResultDto resultDto = resultToResultDto.map(resultBuilder.build());
        assertEquals(2, resultDto.getMovableDemandResults().size());

        assertEquals(76L, resultDto.getMovableDemandResults().get(0).getId());
        assertEquals("movableDemand1", resultDto.getMovableDemandResults().get(0).getName());
        assertEquals(0, resultDto.getMovableDemandResults().get(0).getStartInterval());

        assertEquals(78L, resultDto.getMovableDemandResults().get(1).getId());
        assertEquals("movableDemand2", resultDto.getMovableDemandResults().get(1).getName());
        assertEquals(1, resultDto.getMovableDemandResults().get(1).getStartInterval());
    }

    private Result.ResultBuilder getBaseResultBuilder() {

        return Result.builder()
                .id(1L)
                .optimizationStatus(OptimizationStatus.SOLUTION_FOUND)
                .objectiveFunctionValue(25.0)
                .relativeGap(1e-11)
                .elapsedTime(12.0)
                .errorMessage("");
    }
}
