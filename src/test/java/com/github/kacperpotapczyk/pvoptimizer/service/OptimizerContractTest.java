package com.github.kacperpotapczyk.pvoptimizer.service;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractDirection;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Demand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Production;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.SumConstraint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OptimizerContractTest {

    @Autowired
    public Optimizer optimizer;
    private final ResultValidator resultValidator = new ResultValidator();

    @Test
    void simpleOnePurchase() {
        // one purchase contract available at all intervals
        // at all intervals production is lower than demand by 5
        // at all intervals purchased power have to be 5
        // unit price is 2 thus total cost = 5 * 3 * 2 = 30

        Profile intervals = new Profile(3, 1.0);

        Profile productionProfile = new Profile(3, 5.0);
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(3, 10.0);
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile unitPrice = new Profile(3, 2.0);
        Contract purchaseContract = new Contract(1, "purchase", unitPrice, ContractDirection.PURCHASE);

        Task task = Task.builder()
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(30.0, result.getObjectiveFunctionValue(), 1e-6);

        List<ContractResult> contractResults = result.getContractResults();
        assertEquals(1, contractResults.size());

        List<Profile> expectedPowerResults = new ArrayList<>();
        expectedPowerResults.add(new Profile(Arrays.asList(5.0, 5.0, 5.0)));

        List<Profile> expectedCostResults = new ArrayList<>();
        expectedCostResults.add(new Profile(Arrays.asList(10.0, 10.0, 10.0)));

        resultValidator.assertContractResults(contractResults, expectedPowerResults, expectedCostResults);

        assertEquals(1e-11, result.getRelativeGap());
    }

    @Test
    void simpleTwoPurchases() {
        // first purchase contract cheaper at interval 1 and 2
        // second purchase contract cheaper at interval 3
        // at all intervals production is lower than demand by 5
        // at all intervals purchased power have to be 5
        // best unit price is 2 thus total cost = 5 * 3 * 2 = 30

        Profile intervals = new Profile(3, 1.0);

        Profile productionProfile = new Profile(3, 5.0);
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(3, 10.0);
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile unitPrice1 = new Profile(Arrays.asList(2.0, 2.0, 10.0));
        Contract purchaseContract1 = new Contract(1, "purchase", unitPrice1, ContractDirection.PURCHASE);

        Profile unitPrice2 = new Profile(Arrays.asList(10.0, 10.0, 2.0));
        Contract purchaseContract2 = new Contract(2, "purchase", unitPrice2, ContractDirection.PURCHASE);

        Task task = Task.builder()
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract1)
                .contract(purchaseContract2)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(30.0, result.getObjectiveFunctionValue(), 1e-6);

        List<Profile> expectedPowerResults = new ArrayList<>();
        expectedPowerResults.add(new Profile(Arrays.asList(5.0, 5.0, 0.0)));
        expectedPowerResults.add(new Profile(Arrays.asList(0.0, 0.0, 5.0)));

        List<Profile> expectedCostResults = new ArrayList<>();
        expectedCostResults.add(new Profile(Arrays.asList(10.0, 10.0, 0.0)));
        expectedCostResults.add(new Profile(Arrays.asList(0.0, 0.0, 10.0)));

        List<ContractResult> contractResults = result.getContractResults();

        resultValidator.assertContractResults(contractResults, expectedPowerResults, expectedCostResults);
    }

    @Test
    void simpleSolveTwoSales() {
        // first sell contract is better at interval 1 and 2
        // second sell contract is better at interval 3
        // at all intervals production is greater than demand by 5
        // at all intervals sold power have to be 5
        // best unit price is 2 thus total cost = 5 * 3 * 2 = -30

        Profile intervals = new Profile(3, 1.0);

        Profile productionProfile = new Profile(3, 10.0);
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(3, 5.0);
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile unitPrice1 = new Profile(Arrays.asList(2.0, 2.0, 1.0));
        Contract sellContract1 = new Contract(1, "sell_1", unitPrice1, ContractDirection.SELL);

        Profile unitPrice2 = new Profile(Arrays.asList(1.0, 1.0, 2.0));
        Contract sellContract2 = new Contract(2, "sell_2", unitPrice2, ContractDirection.SELL);

        Task task = Task.builder()
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(sellContract1)
                .contract(sellContract2)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(-30.0, result.getObjectiveFunctionValue(), 1e-6);

        List<Profile> expectedPowerResults = new ArrayList<>();
        expectedPowerResults.add(new Profile(Arrays.asList(5.0, 5.0, 0.0)));
        expectedPowerResults.add(new Profile(Arrays.asList(0.0, 0.0, 5.0)));

        List<Profile> expectedCostResults = new ArrayList<>();
        expectedCostResults.add(new Profile(Arrays.asList(10.0, 10.0, 0.0)));
        expectedCostResults.add(new Profile(Arrays.asList(0.0, 0.0, 10.0)));

        List<ContractResult> contractResults = result.getContractResults();
        resultValidator.assertContractResults(contractResults, expectedPowerResults, expectedCostResults);
    }

    @Test
    public void contractPowerLimits() {
        // first purchase contract is better but its power is limited to 3 at intervals 1 and 3
        // second purchase contract is worse at all intervals but its power must be at least 2 at interval 2
        // at all intervals demand is greater than demand by 5
        // at all intervals purchased power have to be 5, 3 from contract 1 and 2 from contract 2
        // unit price of contract 1 is 2
        // unit price of contract 2 is 10
        // expected objective is (2*3 + 2*10) * 3 = 78

        Profile intervals = new Profile(3, 1.0);

        Profile productionProfile = new Profile(3, 5.0);
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(3, 10.0);
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile unitPrice1 = new Profile(Arrays.asList(2.0, 2.0, 2.0));
        Contract purchaseContract1 = Contract.builder(1, "purchase", unitPrice1, ContractDirection.PURCHASE)
                .maxPowerConstraint(0, 3.0)
                .maxPowerConstraint(2, 3.0)
                .build();

        Profile unitPrice2 = new Profile(Arrays.asList(10.0, 10.0, 10.0));
        Contract purchaseContract2 = Contract.builder(2, "purchase", unitPrice2, ContractDirection.PURCHASE)
                .minPowerConstraint(1, 2.0)
                .build();

        Task task = Task.builder()
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract1)
                .contract(purchaseContract2)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(78.0, result.getObjectiveFunctionValue(), 1e-6);

        List<Profile> expectedPowerResults = new ArrayList<>();
        expectedPowerResults.add(new Profile(Arrays.asList(3.0, 3.0, 3.0)));
        expectedPowerResults.add(new Profile(Arrays.asList(2.0, 2.0, 2.0)));

        List<Profile> expectedCostResults = new ArrayList<>();
        expectedCostResults.add(new Profile(Arrays.asList(6.0, 6.0, 6.0)));
        expectedCostResults.add(new Profile(Arrays.asList(20.0, 20.0, 20.0)));

        List<ContractResult> contractResults = result.getContractResults();

        resultValidator.assertContractResults(contractResults, expectedPowerResults, expectedCostResults);
    }

    @Test
    public void contractEnergyLimit() {
        // two purchase contract and one sell
        // sale contract should be maximized, max energy limit is set
        // purchase contract one is better than contract two, but contract 2 has min energy limit
        // production equals demand for simplicity
        // expected result is:
        // purchaseContract1 sum = 10, cost = 20
        // purchaseContract2 sum = 30, cost = 300
        // sellContract1 sum = 40, cost = 200
        // objective function = 120

        Profile intervals = new Profile(3, 1.0);

        Profile productionProfile = new Profile(3, 10.0);
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(3, 10.0);
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile unitPrice1 = new Profile(Arrays.asList(2.0, 2.0, 2.0));
        Contract purchaseContract1 = Contract.builder(1, "purchase", unitPrice1, ContractDirection.PURCHASE)
                .build();

        Profile unitPrice2 = new Profile(Arrays.asList(10.0, 10.0, 10.0));
        Contract purchaseContract2 = Contract.builder(2, "purchase", unitPrice2, ContractDirection.PURCHASE)
                .minEnergyConstraints(List.of(new SumConstraint(0, 2, 30)))
                .build();

        Profile sellPrice = new Profile(Arrays.asList(5.0, 5.0, 5.0));
        Contract sellContract1 = Contract.builder(3, "sell", sellPrice, ContractDirection.SELL)
                .maxEnergyConstraints(List.of(new SumConstraint(0, 2, 40)))
                .build();

        Task task = Task.builder()
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract1)
                .contract(purchaseContract2)
                .contract(sellContract1)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(120.0, result.getObjectiveFunctionValue(), 1e-6);

        List<ContractResult> contractResults = result.getContractResults();

        List<Double> expectedEnergyResults = Arrays.asList(10.0, 30.0, 40.0);
        resultValidator.assertContractEnergyResults(contractResults, expectedEnergyResults);
    }

    @Test
    public void contractEnergyLimitFifteenMinutesIntervals() {
        // intervals with 0.25h (15 min)
        // two purchase contract and one sell
        // sale contract should be maximized, max energy limit is set
        // purchase contract one is better than contract two, but contract 2 has min energy limit
        // production equals demand for simplicity
        // expected result is:
        // purchaseContract1 sum = 2.5, cost = 5
        // purchaseContract2 sum = 7.5, cost = 75
        // sellContract1 sum = 10, cost = 50
        // objective function = 30

        Profile intervals = new Profile(3, 0.25);

        Profile productionProfile = new Profile(3, 10.0);
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(3, 10.0);
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile unitPrice1 = new Profile(Arrays.asList(2.0, 2.0, 2.0));
        Contract purchaseContract1 = Contract.builder(1, "purchase", unitPrice1, ContractDirection.PURCHASE)
                .build();

        Profile unitPrice2 = new Profile(Arrays.asList(10.0, 10.0, 10.0));
        Contract purchaseContract2 = Contract.builder(2, "purchase", unitPrice2, ContractDirection.PURCHASE)
                .minEnergyConstraints(List.of(new SumConstraint(0, 2, 7.5)))
                .build();

        Profile sellPrice = new Profile(Arrays.asList(5.0, 5.0, 5.0));
        Contract sellContract1 = Contract.builder(3, "sell", sellPrice, ContractDirection.SELL)
                .maxEnergyConstraints(List.of(new SumConstraint(0, 2, 10)))
                .build();

        Task task = Task.builder()
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract1)
                .contract(purchaseContract2)
                .contract(sellContract1)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(30.0, result.getObjectiveFunctionValue(), 1e-6);

        List<ContractResult> contractResults = result.getContractResults();

        List<Double> expectedEnergyResults = Arrays.asList(2.5, 7.5, 10.0);
        resultValidator.assertContractEnergyResults(contractResults, expectedEnergyResults);
    }

    @Test
    void contractLongerThanOptimizationHorizon() {
        // one purchase contract available at intervals 0 to 4 witch is longer than task optimization horizon
        // at all intervals production is lower than demand by 5
        // at all intervals purchased power have to be 5
        // unit price is 2 thus total cost = 5 * 3 * 2 = 30

        Profile intervals = new Profile(3, 1.0);

        Profile productionProfile = new Profile(3, 5.0);
        Production production = new Production(1, "pv production", productionProfile);

        Profile demandProfile = new Profile(3, 10.0);
        Demand demand = new Demand(1, "home demand", demandProfile);

        Profile unitPrice = new Profile(5, 2.0);
        Map<Integer, Double> maxPowerConstraints = new HashMap<>(5);
        maxPowerConstraints.put(0, 5.0);
        maxPowerConstraints.put(1, 6.0);
        maxPowerConstraints.put(2, 7.0);
        maxPowerConstraints.put(3, 8.0);
        maxPowerConstraints.put(4, 9.0);

        Contract purchaseContract = Contract.builder(1, "purchase", unitPrice, ContractDirection.PURCHASE)
                .maxPowerConstraints(maxPowerConstraints)
                .build();


        Task task = Task.builder()
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(30.0, result.getObjectiveFunctionValue(), 1e-6);

        List<ContractResult> contractResults = result.getContractResults();

        List<Profile> expectedPowerResults = new ArrayList<>();
        expectedPowerResults.add(new Profile(Arrays.asList(5.0, 5.0, 5.0)));

        List<Profile> expectedCostResults = new ArrayList<>();
        expectedCostResults.add(new Profile(Arrays.asList(10.0, 10.0, 10.0)));

        resultValidator.assertContractResults(contractResults, expectedPowerResults, expectedCostResults);
    }
}
