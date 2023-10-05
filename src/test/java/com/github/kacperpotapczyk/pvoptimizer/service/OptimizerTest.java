package com.github.kacperpotapczyk.pvoptimizer.service;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractDirection;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Demand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Production;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.Storage;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OptimizerTest {

    @Autowired
    public Optimizer optimizer;
    private final ResultValidator resultValidator = new ResultValidator();

    @Test
    public void completeTest() {

        Profile intervals = new Profile(24, 1.0);
        double relativeGap = 0.001;
        long maxTime = 60L;

        Profile productionProfile = new Profile(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.05, 0.05, 0.05, 0.05, 0.2, 0.2, 0.2, 0.2, 0.2, 0.05, 0.05, 0.05, 0.05, 0.0, 0.0, 0.0, 0.0));
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(Arrays.asList(0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.145, 0.145, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045, 0.045));
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile purchasePrice = new Profile(Arrays.asList(0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.37, 0.37));
        Contract purchaseContract = Contract.builder(1, "purchase", purchasePrice, ContractDirection.PURCHASE)
                .build();

        Profile sellPrice = new Profile(Arrays.asList(0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2));
        Contract sellContract1 = Contract.builder(2, "sell", sellPrice, ContractDirection.SELL)
                .build();

        MovableDemand movableDemand = new MovableDemand(1, "movable demand", Arrays.asList(0.3, 0.3, 0.3), Set.of(19, 20, 21));

        double minEnergy = 0.25;
        Storage storage = Storage.builder(1, "Storage", 0.3, 0.3, 2.0)
                .initialEnergy(minEnergy)
                .minEnergyConstraints(IntStream.range(0, 24).boxed()
                        .collect(Collectors.toMap(
                                Function.identity(),
                                integer -> minEnergy
                )))
                .forbiddenChargeInterval(12)
                .forbiddenDischargeInterval(12)
                .build();

        Task task = Task.builder()
                .id(1L)
                .timeoutSeconds(maxTime)
                .relativeGap(relativeGap)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract)
                .contract(sellContract1)
                .storage(storage)
                .movableDemand(movableDemand)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(0.3244, result.getObjectiveFunctionValue(), 1e-6);

        List<ContractResult> contractResults = result.getContractResults();

        List<Double> expectedEnergyResults = Arrays.asList(0.935, 0.155);
        resultValidator.assertContractEnergyResults(contractResults, expectedEnergyResults);

        StorageResult storageResult = result.getStorageResults().get(0);
        assertEquals(0.78, storageResult.energy().getValueForInterval(5).orElse(-1.0), 1e-6);
        assertEquals(1.15, storageResult.energy().getValueForInterval(20).orElse(-1.0), 1e-6);
        assertEquals(minEnergy, storageResult.energy().getValueForInterval(23).orElse(-1.0), 1e-6);

        List<Integer> expectedMovableDemandStartIntervals = new ArrayList<>();
        expectedMovableDemandStartIntervals.add(21);

        resultValidator.assertMovableDemandResults(expectedMovableDemandStartIntervals, result.getMovableDemandResults());
        assertEquals(relativeGap, result.getRelativeGap(), 1e-6);
        assertTrue(result.getElapsedTime() >= 1e-6);
        assertTrue(result.getElapsedTime() < maxTime);
    }

    @Test
    @Disabled("Long running test (5 minutes).")
    public void completeTestFifteenMinutesIntervals() {

        Profile intervals = new Profile(96, 0.25);

        Profile productionProfile = new Profile(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.00048, 0.00015, 0.00072, 0.01442, 0.01443, 0.01448, 0.01371, 0.05276, 0.05076, 0.05624, 0.05361, 0.11615, 0.11249, 0.1204, 0.11963, 0.17364, 0.17434, 0.17665, 0.179, 0.21132, 0.21337, 0.22272, 0.23063, 0.26012, 0.25388, 0.2462, 0.25408, 0.24615, 0.2418, 0.25684, 0.25761, 0.23229, 0.22544, 0.22527, 0.24091, 0.20911, 0.19972, 0.20618, 0.20437, 0.16071, 0.14794, 0.14938, 0.15199, 0.09537, 0.09572, 0.09636, 0.1029, 0.04131, 0.04205, 0.0397, 0.04017, 0.00232, 0.00332, 0.00317, 0.00267, 0.00076, 0.00094, 0.00025, 0.00009, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(Arrays.asList(0.04771, 0.0051, 0.00508, 0.00542, 0.04912, 0.00622, 0.00512, 0.00531, 0.04738, 0.00537, 0.00639, 0.00613, 0.04977, 0.00518, 0.00563, 0.00516, 0.04985, 0.00542, 0.0058, 0.00534, 0.04872, 0.00517, 0.00612, 0.00517, 0.14607, 0.19371, 0.19799, 0.19605, 0.19014, 0.17649, 0.13251, 0.12018, 0.04772, 0.00641, 0.00572, 0.0063, 0.04974, 0.00553, 0.00613, 0.00612, 0.04834, 0.00524, 0.00618, 0.00618, 0.04568, 0.00583, 0.00596, 0.00584, 0.04917, 0.00632, 0.00524, 0.00643, 0.04672, 0.0053, 0.00588, 0.00545, 0.04908, 0.0055, 0.00528, 0.00505, 0.04984, 0.1258, 0.0508, 0.0539, 0.09623, 0.05599, 0.05229, 0.05305, 0.1033, 0.05104, 0.05174, 0.05001, 0.13093, 0.07681, 0.08107, 0.08307, 0.12444, 0.08147, 0.07751, 0.07991, 0.13223, 0.07862, 0.08238, 0.07767, 0.1323, 0.07892, 0.07532, 0.08146, 0.12531, 0.07558, 0.07694, 0.08248, 0.05018, 0.00534, 0.0052, 0.00649));
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile purchasePrice = new Profile(Arrays.asList(0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37));
        Contract purchaseContract = Contract.builder(1, "purchase", purchasePrice, ContractDirection.PURCHASE)
                .build();

        Profile sellPrice = new Profile(Arrays.asList(0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2));
        Contract sellContract1 = Contract.builder(2, "sell", sellPrice, ContractDirection.SELL)
                .build();

        MovableDemand movableDemand = new MovableDemand(1, "movable demand", Arrays.asList(0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3), Set.of(81, 82, 83, 84));

        double minEnergy = 0.25;
        Storage storage = Storage.builder(1, "Storage", 0.3, 0.3, 2.0)
                .initialEnergy(minEnergy)
                .minEnergyConstraints(IntStream.range(0, 96).boxed()
                        .collect(Collectors.toMap(
                                Function.identity(),
                                integer -> minEnergy
                        )))
                .build();

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract)
                .contract(sellContract1)
                .storage(storage)
                .movableDemand(movableDemand)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(0.186782375, result.getObjectiveFunctionValue(), 1e-4);
    }
}