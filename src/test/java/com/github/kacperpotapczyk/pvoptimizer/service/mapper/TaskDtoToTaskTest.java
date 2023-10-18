package com.github.kacperpotapczyk.pvoptimizer.service.mapper;

import com.github.kacperpotapczyk.pvoptimizer.dto.*;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractDirection;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemand;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.Storage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TaskDtoToTaskTest {

    private final Mapper<TaskDto, Task> taskDtoToTask;

    @Autowired
    public TaskDtoToTaskTest(Mapper<TaskDto, Task> taskDtoToTask) {
        this.taskDtoToTask = taskDtoToTask;
    }

    @Test
    public void testBaseTask() {

        long taskId = 1L;
        List<Double> intervals = Arrays.asList(1.0, 1.0);
        List<Double> demandProfile = Arrays.asList(1.0, 1.0);
        List<Double> productionProfile = Arrays.asList(1.0, 1.0);

        TaskDto taskDto = getBaseTaskBuilder(demandProfile, productionProfile, taskId, intervals).build();

        Task task = taskDtoToTask.map(taskDto);

        assertNotNull(task);
        assertEquals(1L, task.getId());
        assertEquals(0.0, task.getRelativeGap());
        assertEquals(0L, task.getTimeoutSeconds());
        assertEquals(2, task.optimizationHorizonLength());
        assertEquals(1.0, task.getDemand().getProfile().getValueForInterval(0).orElse(-1.0));
        assertEquals(1.0, task.getProduction().getProfile().getValueForInterval(1).orElse(-1.0));
        assertEquals(Collections.EMPTY_SET, task.getContracts());
        assertEquals(Collections.EMPTY_SET, task.getStorages());
        assertEquals(Collections.EMPTY_SET, task.getMovableDemands());
    }

    @Test
    public void TestTaskWithContract() {

        long taskId = 1L;
        List<Double> intervals = Arrays.asList(1.0, 1.0);
        List<Double> demandProfile = Arrays.asList(1.0, 1.0);
        List<Double> productionProfile = Arrays.asList(1.0, 1.0);

        TaskDto.Builder taskDtoBuilder = getBaseTaskBuilder(demandProfile, productionProfile, taskId, intervals);

        long purchaseContractId = 223L;
        List<Double> purchaseUnitPrice = Arrays.asList(20.0, 15.0);

        Map<CharSequence, Double> minPowerConstraint = new HashMap<>();
        minPowerConstraint.put("0", 2.0);

        Map<CharSequence, Double> maxPowerConstraint = new HashMap<>();
        maxPowerConstraint.put("0", 6.0);

        SumConstraintDto maxEnergyConstraint = new SumConstraintDto(1, 1, 12.0);

        ContractDto contractDto = ContractDto.newBuilder()
                .setId(purchaseContractId)
                .setName("Purchase")
                .setContractDirection(ContractDirectionDto.PURCHASE)
                .setUnitPrice(purchaseUnitPrice)
                .setMinPower(minPowerConstraint)
                .setMaxPower(maxPowerConstraint)
                .setMaxEnergy(List.of(maxEnergyConstraint))
                .build();

        taskDtoBuilder.setContracts(List.of(contractDto));

        Task task = taskDtoToTask.map(taskDtoBuilder.build());
        assertEquals(1, task.getContracts().size());

        Contract contract = task.getContracts().stream().findAny().orElseThrow();
        assertEquals(purchaseContractId, contract.getId());
        assertEquals("Purchase", contract.getName());
        assertEquals(0, contract.getStartInterval());
        assertEquals(ContractDirection.PURCHASE, contract.getContractDirection());
        assertEquals(2, contract.getContractLength());
        assertEquals(purchaseUnitPrice, contract.getUnitPrice().getValues());
        assertEquals(Map.ofEntries(Map.entry(0, 2.0)), contract.getMinPowerConstraints());
        assertEquals(Map.ofEntries(Map.entry(0, 6.0)), contract.getMaxPowerConstraints());
        assertNull(contract.getMinEnergyConstraints());
        assertEquals(1, contract.getMaxEnergyConstraints().size());
        assertEquals(1, contract.getMaxEnergyConstraints().get(0).startInterval());
        assertEquals(1, contract.getMaxEnergyConstraints().get(0).endInterval());
    }

    @Test
    public void TestTaskWithStorages() {

        long taskId = 1L;
        List<Double> intervals = Arrays.asList(1.0, 1.0);
        List<Double> demandProfile = Arrays.asList(1.0, 1.0);
        List<Double> productionProfile = Arrays.asList(1.0, 1.0);

        TaskDto.Builder taskDtoBuilder = getBaseTaskBuilder(demandProfile, productionProfile, taskId, intervals);

        StorageDto storageDto1 = StorageDto.newBuilder()
                .setId(1L)
                .setName("Simple")
                .setInitialEnergy(10.0)
                .setMaxCharge(1.0)
                .setMaxDischarge(2.0)
                .setMaxCapacity(20.0)
                .build();

        StorageDto storageDto2 = StorageDto.newBuilder()
                .setId(2L)
                .setName("Complex")
                .setInitialEnergy(10.0)
                .setMaxCharge(1.0)
                .setMaxDischarge(2.0)
                .setMaxCapacity(20.0)
                .setMinDischargeConstraints(Map.ofEntries(Map.entry("1", 1.0)))
                .setMaxChargeConstraints(Map.ofEntries(Map.entry("0", 0.5)))
                .setMinEnergyConstraints(Map.ofEntries(Map.entry("1", 9.0)))
                .setMaxEnergyConstraints(Map.ofEntries(Map.entry("1", 15.0)))
                .build();

        taskDtoBuilder
                .setStorages(Arrays.asList(storageDto1, storageDto2));

        Task task = taskDtoToTask.map(taskDtoBuilder.build());

        assertEquals(2, task.getStorages().size());

        Storage storage1 = task.getStorages().stream()
                .filter(storage -> storage.getId() == 1L)
                .findAny().orElseThrow();

        Storage storage2 = task.getStorages().stream()
                .filter(storage -> storage.getId() == 2L)
                .findAny().orElseThrow();

        assertEquals("Simple", storage1.getName());
        assertEquals(10.0, storage1.getInitialEnergy());
        assertEquals(1.0, storage1.getMaxCharge());
        assertEquals(2.0, storage1.getMaxDischarge());
        assertEquals(20.0, storage1.getMaxCapacity());

        assertEquals("Complex", storage2.getName());
        assertEquals(Map.ofEntries(Map.entry(1, 1.0)), storage2.getMinDischargeConstraints());
        assertEquals(Map.ofEntries(Map.entry(0, 0.5)), storage2.getMaxChargeConstraints());
        assertEquals(Map.ofEntries(Map.entry(1, 9.0)), storage2.getMinEnergyConstraints());
        assertEquals(Map.ofEntries(Map.entry(1, 15.0)), storage2.getMaxEnergyConstraints());
    }

    @Test
    public void TestTaskWithMovableDemand() {

        long taskId = 1L;
        List<Double> intervals = Arrays.asList(1.0, 1.0);
        List<Double> demandProfile = Arrays.asList(1.0, 1.0);
        List<Double> productionProfile = Arrays.asList(1.0, 1.0);

        TaskDto.Builder taskDtoBuilder = getBaseTaskBuilder(demandProfile, productionProfile, taskId, intervals);

        List<Double> movableDemandProfile = Arrays.asList(0.5, 0.75);
        List<Integer> movableDemandStartIntervals = Arrays.asList(0, 1);
        MovableDemandDto movableDemandDto = new MovableDemandDto(
                42L,
                "movableDemand",
                movableDemandProfile,
                movableDemandStartIntervals
        );

        taskDtoBuilder.setMovableDemands(List.of(movableDemandDto));

        Task task = taskDtoToTask.map(taskDtoBuilder.build());

        assertEquals(1, task.getMovableDemands().size());

        MovableDemand movableDemand = task.getMovableDemands().stream().findAny().orElseThrow();
        assertEquals(42L, movableDemand.id());
        assertEquals("movableDemand", movableDemand.name());
        assertEquals(movableDemandProfile, movableDemand.profile());
        assertEquals(new HashSet<>(movableDemandStartIntervals), movableDemand.startIntervals());
    }

    private TaskDto.Builder getBaseTaskBuilder(List<Double> demandProfile, List<Double> productionProfile, long taskId, List<Double> intervals) {

        DemandDto demand = DemandDto.newBuilder()
                .setId(1L)
                .setName("Test demand")
                .setDemandProfile(demandProfile)
                .build();

        ProductionDto production = ProductionDto.newBuilder()
                .setId(1L)
                .setName("Test production")
                .setProductionProfile(productionProfile)
                .build();

        return TaskDto.newBuilder()
                .setId(taskId)
                .setIntervals(intervals)
                .setDemand(demand)
                .setProduction(production);
    }
}
