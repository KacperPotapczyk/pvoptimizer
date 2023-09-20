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

@SpringBootTest
class OptimizerTest {

    @Autowired
    public Optimizer optimizer;
    private final ResultValidator resultValidator = new ResultValidator();

    @Test
    public void completeTest() {

        Profile intervals = new Profile(24, 1.0);

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
    }

    @Test
    public void completeTestFifteenMinutesIntervals() {

        Profile intervals = new Profile(96, 0.25);

        Profile productionProfile = new Profile(Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.00015, 0.00044, 0.00018, 0.0147, 0.01383, 0.01561, 0.01527, 0.05643, 0.05569, 0.05274, 0.05123, 0.11518, 0.11471, 0.11974, 0.11325, 0.18029, 0.17649, 0.16653, 0.16708, 0.23129, 0.2259, 0.2229, 0.21537, 0.2444, 0.24753, 0.24207, 0.25948, 0.25284, 0.24893, 0.25951, 0.24448, 0.22372, 0.2217, 0.22901, 0.23143, 0.19613, 0.19258, 0.20015, 0.19212, 0.1592, 0.15843, 0.15919, 0.15582, 0.09496, 0.09736, 0.0974, 0.1015, 0.04176, 0.03956, 0.03847, 0.03979, 0.00262, 0.00336, 0.00246, 0.00299, 0.00085, 0.0003, 0.00098, 0.00098, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0));
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(Arrays.asList(0.04858, 0.00599, 0.00642, 0.00608, 0.0456, 0.00614, 0.00615, 0.00649, 0.0462, 0.00502, 0.00531, 0.00532, 0.04937, 0.00641, 0.00596, 0.00554, 0.04578, 0.00514, 0.00526, 0.00584, 0.04798, 0.00565, 0.00567, 0.00534, 0.14749, 0.19709, 0.19931, 0.19852, 0.19751, 0.18104, 0.13154, 0.12025, 0.04736, 0.0062, 0.00509, 0.00641, 0.04621, 0.00533, 0.00603, 0.00638, 0.04672, 0.00567, 0.00606, 0.00636, 0.04819, 0.00566, 0.00617, 0.00644, 0.04609, 0.00597, 0.00512, 0.00535, 0.04868, 0.0059, 0.00594, 0.0056, 0.04711, 0.00534, 0.00623, 0.00564, 0.05027, 0.12917, 0.05235, 0.05021, 0.0969, 0.05435, 0.05451, 0.05204, 0.09634, 0.05563, 0.05447, 0.05172, 0.12758, 0.07977, 0.07878, 0.07512, 0.12523, 0.08294, 0.07995, 0.08285, 0.12857, 0.08062, 0.07556, 0.08167, 0.12595, 0.07816, 0.08229, 0.38217, 0.12264, 0.07857, 0.07617, 0.08038, 0.04873, 0.00633, 0.00582, 0.0065));
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile purchasePrice = new Profile(Arrays.asList(0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.58, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37, 0.37));
        Contract purchaseContract = Contract.builder(1, "purchase", purchasePrice, ContractDirection.PURCHASE)
                .build();

        Profile sellPrice = new Profile(Arrays.asList(0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2, 0.2));
        Contract sellContract1 = Contract.builder(2, "sell", sellPrice, ContractDirection.SELL)
                .build();

        MovableDemand movableDemand = new MovableDemand(1, "movable demand", Arrays.asList(0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3), Set.of(72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83));

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


        List<ContractResult> contractResults = result.getContractResults();
        StorageResult storageResult = result.getStorageResults().get(0);

        assertEquals(0.186350525, result.getObjectiveFunctionValue(), 1e-2);

        // TODO correct expected sell and purchase energy
        List<Double> expectedEnergyResults = Arrays.asList(2.49173, 1.200);
        resultValidator.assertContractEnergyResults(contractResults, expectedEnergyResults);


        assertEquals(minEnergy, storageResult.energy().getValueForInterval(95).orElse(-1.0), 1e-6);

        List<Integer> expectedMovableDemandStartIntervals = new ArrayList<>();
        expectedMovableDemandStartIntervals.add(83);

        resultValidator.assertMovableDemandResults(expectedMovableDemandStartIntervals, result.getMovableDemandResults());
    }
}