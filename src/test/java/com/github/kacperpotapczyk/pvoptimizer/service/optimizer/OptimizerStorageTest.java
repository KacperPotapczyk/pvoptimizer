package com.github.kacperpotapczyk.pvoptimizer.service.optimizer;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractDirection;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Demand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Production;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.Storage;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageMode;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageModeProfile;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.StorageResult;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OptimizerStorageTest {

    @Autowired
    public Optimizer optimizer;
    private final ResultValidator resultValidator = new ResultValidator();

    @Test
    public void simpleStorage() {
        // at interval 1 and 2 production exceeds demand by 5
        // at interval 3 demand is greater than production by 10
        // excess energy has to be stored in storage

        Profile intervals = new Profile(3, 1.0);

        Profile productionProfile = new Profile(Arrays.asList(10.0, 10.0, 0.0));
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(Arrays.asList(5.0, 5.0, 10.0));
        Demand demand = new Demand(1, "home demand", demandProfile);

        Storage storage = Storage.builder(1, "Storage", 20.0, 20.0, 40.0)
                .build();

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .storage(storage)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());

        List<Profile> expectedCharge = new ArrayList<>();
        expectedCharge.add(new Profile(Arrays.asList(5.0, 5.0, 0.0)));

        List<Profile> expectedDischarge = new ArrayList<>();
        expectedDischarge.add(new Profile(Arrays.asList(0.0, 0.0, 10.0)));

        List<Profile> expectedEnergy = new ArrayList<>();
        expectedEnergy.add(new Profile(Arrays.asList(5.0, 10.0, 0.0)));

        List<StorageModeProfile> expectedMode = new ArrayList<>();
        expectedMode.add(new StorageModeProfile(Arrays.asList(StorageMode.CHARGING, StorageMode.CHARGING, StorageMode.DISCHARGING)));

        List<StorageResult> storageResults = result.getStorageResults();

        resultValidator.assertStorageResults(expectedCharge, expectedDischarge, expectedEnergy, expectedMode, storageResults);
    }

    @Test
    public void storageDischargeConstraints() {
        // demand and production are 0
        // there is initial 40 energy in storage
        // it is optimal to sell this energy using sell contract
        // storage discharge is limited to 10 at all intervals by discharge constraints
        // minimal energy at interval 2 is 25
        // and at last interval discharge is forbidden

        Profile intervals = new Profile(3, 1.0);
        Production production = new Production(1, "pv production", new Profile(3, 0.0));
        Demand demand = new Demand(1, "home demand", new Profile(3, 0.0));

        Map<Integer, Double> maxDischarge = IntStream.range(0, 3)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> 10.0
                ));

        Storage storage = Storage.builder(1, "Storage", 20.0, 20.0, 40.0)
                .initialEnergy(40.0)
                .maxDischargeConstraints(maxDischarge)
                .minEnergyConstraint(1, 25.0)
                .forbiddenDischargeInterval(2)
                .build();

        Profile sellPrice = new Profile(Arrays.asList(10.0, 5.0, 5.0)); // should maximize sell at 1 interval
        Contract sellContract = Contract.builder(3, "sell", sellPrice, ContractDirection.SELL)
                .build();

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .storage(storage)
                .contract(sellContract)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());

        List<Profile> expectedCharge = new ArrayList<>();
        expectedCharge.add(new Profile(Arrays.asList(0.0, 0.0, 0.0)));

        List<Profile> expectedDischarge = new ArrayList<>();
        expectedDischarge.add(new Profile(Arrays.asList(10.0, 5.0, 0.0)));

        List<Profile> expectedEnergy = new ArrayList<>();
        expectedEnergy.add(new Profile(Arrays.asList(30.0, 25.0, 25.0)));

        List<StorageModeProfile> expectedMode = new ArrayList<>();
        expectedMode.add(new StorageModeProfile(Arrays.asList(StorageMode.DISCHARGING, StorageMode.DISCHARGING, StorageMode.DISABLED)));

        List<StorageResult> storageResults = result.getStorageResults();

        resultValidator.assertStorageResults(expectedCharge, expectedDischarge, expectedEnergy, expectedMode, storageResults);
    }

    @Test
    public void storageDischargeConstraintsFifteenMinutesIntervals() {
        // demand and production are 0
        // there is initial 40 energy in storage
        // it is optimal to sell this energy using sell contract
        // storage discharge is limited to 10 at all intervals by discharge constraints
        // minimal energy at interval 2 is 26
        // and at last interval discharge is forbidden

        Profile intervals = new Profile(3, 0.25);
        Production production = new Production(1, "pv production", new Profile(3, 0.0));
        Demand demand = new Demand(1, "home demand", new Profile(3, 0.0));

        Map<Integer, Double> maxDischarge = IntStream.range(0, 3)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> 10.0
                ));

        Storage storage = Storage.builder(1, "Storage", 20.0, 20.0, 40.0)
                .initialEnergy(40.0)
                .maxDischargeConstraints(maxDischarge)
                .minEnergyConstraint(1, 36.0)
                .forbiddenDischargeInterval(2)
                .build();

        Profile sellPrice = new Profile(Arrays.asList(10.0, 5.0, 5.0)); // should maximize sell at 1 interval
        Contract sellContract = Contract.builder(3, "sell", sellPrice, ContractDirection.SELL)
                .build();

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .storage(storage)
                .contract(sellContract)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());

        List<Profile> expectedCharge = new ArrayList<>();
        expectedCharge.add(new Profile(Arrays.asList(0.0, 0.0, 0.0)));

        List<Profile> expectedDischarge = new ArrayList<>();
        expectedDischarge.add(new Profile(Arrays.asList(10.0, 6.0, 0.0)));

        List<Profile> expectedEnergy = new ArrayList<>();
        expectedEnergy.add(new Profile(Arrays.asList(37.5, 36.0, 36.0)));

        List<StorageModeProfile> expectedMode = new ArrayList<>();
        expectedMode.add(new StorageModeProfile(Arrays.asList(StorageMode.DISCHARGING, StorageMode.DISCHARGING, StorageMode.DISABLED)));

        List<StorageResult> storageResults = result.getStorageResults();

        resultValidator.assertStorageResults(expectedCharge, expectedDischarge, expectedEnergy, expectedMode, storageResults);
    }

    @Test
    public void storageChargeConstraints() {
        // demand and production are 0
        // there is initial 0 energy in storage
        // purchase contract is available at intervals 0, 1 and 2
        // sell contract is available at interval 3, and it is optimal to sell all available energy
        // storage charge is limited to 10 at all intervals by charge constraints
        // maximal energy at interval 2 is 15
        // and at first interval charge is forbidden

        Profile intervals = new Profile(4, 1.0);
        Production production = new Production(1, "pv production", new Profile(4, 0.0));
        Demand demand = new Demand(1, "home demand", new Profile(4, 0.0));

        Map<Integer, Double> maxCharge = IntStream.range(0, 4)
                .boxed()
                .collect(Collectors.toMap(
                        i -> i,
                        i -> 10.0
                ));

        Storage storage = Storage.builder(1, "Storage", 20.0, 20.0, 40.0)
                .initialEnergy(0.0)
                .maxChargeConstraints(maxCharge)
                .maxEnergyConstraint(2, 15.0)
                .forbiddenChargeInterval(0)
                .build();

        Profile purchaseCost = new Profile(0, Arrays.asList(1.0, 3.0, 10.0));
        Contract purchaseContract = Contract.builder(1, "buy", purchaseCost, ContractDirection.PURCHASE)
                .build();

        Profile sellPrice = new Profile(3, List.of(20.0)); // should maximize sell at 3 interval
        Contract sellContract = Contract.builder(3, "sell", sellPrice, ContractDirection.SELL)
                .build();

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .storage(storage)
                .contract(purchaseContract)
                .contract(sellContract)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());

        List<Profile> expectedCharge = new ArrayList<>();
        expectedCharge.add(new Profile(Arrays.asList(0.0, 10.0, 5.0, 0.0)));

        List<Profile> expectedDischarge = new ArrayList<>();
        expectedDischarge.add(new Profile(Arrays.asList(0.0, 0.0, 0.0, 15.0)));

        List<Profile> expectedEnergy = new ArrayList<>();
        expectedEnergy.add(new Profile(Arrays.asList(0.0, 10.0, 15.0, 0.0)));

        List<StorageModeProfile> expectedMode = new ArrayList<>();
        expectedMode.add(new StorageModeProfile(Arrays.asList(StorageMode.DISABLED, StorageMode.CHARGING, StorageMode.CHARGING, StorageMode.DISCHARGING)));

        List<StorageResult> storageResults = result.getStorageResults();

        resultValidator.assertStorageResults(expectedCharge, expectedDischarge, expectedEnergy, expectedMode, storageResults);
    }
}
