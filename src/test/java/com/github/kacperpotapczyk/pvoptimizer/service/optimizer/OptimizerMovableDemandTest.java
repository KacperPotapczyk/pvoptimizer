package com.github.kacperpotapczyk.pvoptimizer.service.optimizer;

import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractDirection;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractResult;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Demand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Production;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.OptimizationStatus;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OptimizerMovableDemandTest {

    @Autowired
    public Optimizer optimizer;
    private final ResultValidator resultValidator = new ResultValidator();

    @Test
    public void movableDemand() {
        // there are 4 intervals, fixed production and fixed demand
        // production exceeds fixed demand at intervals 2 and 3 by 5
        // movable demand with length of 2 and demand value of 5 can be started at interval 0, 2 or 3
        // only feasible solution is to start movable demand at interval 2

        Profile intervals = new Profile(4, 1.0);
        Production production = new Production(1, "pv production", new Profile(Arrays.asList(5.0, 5.0, 10.0, 10.0)));
        Demand demand = new Demand(1, "home demand", new Profile(4, 5.0));
        MovableDemand movableDemand = new MovableDemand(1, "movable demand", Arrays.asList(5.0, 5.0), Set.of(0, 2, 3));

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .movableDemand(movableDemand)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());

        List<Integer> expectedMovableDemandStartIntervals = new ArrayList<>();
        expectedMovableDemandStartIntervals.add(2);

        resultValidator.assertMovableDemandResults(expectedMovableDemandStartIntervals, result.getMovableDemandResults());
        assertEquals("", result.getErrorMessage());
    }

    @Test
    public void movableDemandInfeasible() {
        // there are 4 intervals, fixed production and fixed demand
        // production exceeds fixed demand at intervals 2 and 3 by 5
        // movable demand with length of 2 and demand value of 5 can be started at interval 0, 2 or 3
        // only feasible solution is to start movable demand at interval 2

        Profile intervals = new Profile(4, 1.0);
        Production production = new Production(1, "pv production", new Profile(Arrays.asList(5.0, 5.0, 11.0, 11.0)));
        Demand demand = new Demand(1, "home demand", new Profile(4, 5.0));
        MovableDemand movableDemand = new MovableDemand(1, "movable demand", Arrays.asList(5.0, 5.0), Set.of(0, 2, 3));

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .movableDemand(movableDemand)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_NOT_FOUND, result.getOptimizationStatus());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    public void optimalMovableDemandAllocation() {
        // production and fixed demand equal 0
        // there is purchase contract with unit cost at intervals 0 and 1 equal 10
        // unit cost at intervals 2 and 3 equal 5
        // movable demand profile [2, 4] with allowed start intervals 0 and 2
        // optimal solution is to start movable demand at interval 2
        // expected objective function value 30

        Profile intervals = new Profile(4, 1.0);
        Production production = new Production(1, "pv production", new Profile(4, 0.0));
        Demand demand = new Demand(1, "home demand", new Profile(4, 0.0));
        MovableDemand movableDemand = new MovableDemand(1, "movable demand", Arrays.asList(2.0, 4.0), Set.of(0, 2));

        Profile unitPrice = new Profile(Arrays.asList(10.0, 10.0, 5.0, 5.0));
        Contract purchaseContract = new Contract(1, "purchase", unitPrice, ContractDirection.PURCHASE);

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract)
                .movableDemand(movableDemand)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(30.0, result.getObjectiveFunctionValue(), 1e-6);

        List<ContractResult> contractResults = result.getContractResults();

        List<Profile> expectedPowerResults = new ArrayList<>();
        expectedPowerResults.add(new Profile(Arrays.asList(0.0, 0.0, 2.0, 4.0)));

        List<Profile> expectedCostResults = new ArrayList<>();
        expectedCostResults.add(new Profile(Arrays.asList(0.0, 0.0, 10.0, 20.0)));

        List<Integer> expectedMovableDemandStartIntervals = new ArrayList<>();
        expectedMovableDemandStartIntervals.add(2);

        resultValidator.assertContractResults(contractResults, expectedPowerResults, expectedCostResults);
        resultValidator.assertMovableDemandResults(expectedMovableDemandStartIntervals, result.getMovableDemandResults());
    }

    @Test
    public void optimalMovableDemandAllocationFifteenMinutesIntervals() {
        // production and fixed demand equal 0
        // there is purchase contract with unit cost at intervals 0 and 1 equal 10
        // unit cost at intervals 2 and 3 equal 5
        // movable demand profile [2, 4] with allowed start intervals 0 and 2
        // optimal solution is to start movable demand at interval 2
        // expected objective function value 30

        Profile intervals = new Profile(4, 0.25);
        Production production = new Production(1, "pv production", new Profile(4, 0.0));
        Demand demand = new Demand(1, "home demand", new Profile(4, 0.0));
        MovableDemand movableDemand = new MovableDemand(1, "movable demand", Arrays.asList(2.0, 4.0), Set.of(0, 2));

        Profile unitPrice = new Profile(Arrays.asList(10.0, 10.0, 5.0, 5.0));
        Contract purchaseContract = new Contract(1, "purchase", unitPrice, ContractDirection.PURCHASE);

        Task task = Task.builder()
                .id(1L)
                .intervals(intervals)
                .production(production)
                .demand(demand)
                .contract(purchaseContract)
                .movableDemand(movableDemand)
                .build();

        Result result = optimizer.solve(task);

        assertEquals(OptimizationStatus.SOLUTION_FOUND, result.getOptimizationStatus());
        assertEquals(7.5, result.getObjectiveFunctionValue(), 1e-6);

        List<ContractResult> contractResults = result.getContractResults();

        List<Profile> expectedPowerResults = new ArrayList<>();
        expectedPowerResults.add(new Profile(Arrays.asList(0.0, 0.0, 2.0, 4.0)));

        List<Profile> expectedCostResults = new ArrayList<>();
        expectedCostResults.add(new Profile(Arrays.asList(0.0, 0.0, 2.5, 5.0)));

        List<Integer> expectedMovableDemandStartIntervals = new ArrayList<>();
        expectedMovableDemandStartIntervals.add(2);

        resultValidator.assertContractResults(contractResults, expectedPowerResults, expectedCostResults);
        resultValidator.assertMovableDemandResults(expectedMovableDemandStartIntervals, result.getMovableDemandResults());
    }
}
