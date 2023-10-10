package com.github.kacperpotapczyk.pvoptimizer.service;

import com.github.kacperpotapczyk.pvoptimizer.dto.*;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.Contract;
import com.github.kacperpotapczyk.pvoptimizer.model.contract.ContractDirection;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Demand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.MovableDemand;
import com.github.kacperpotapczyk.pvoptimizer.model.sourcesink.Production;
import com.github.kacperpotapczyk.pvoptimizer.model.storage.Storage;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.Profile;
import com.github.kacperpotapczyk.pvoptimizer.model.utils.SumConstraint;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class TaskDtoToTask implements Mapper<TaskDto, Task> {

    @Override
    public Task map(TaskDto taskDto) {

        Task.TaskBuilder taskBuilder = Task.builder();
        taskBuilder
                .id(taskDto.getId())
                .timeoutSeconds(taskDto.getTimeoutSeconds())
                .relativeGap(taskDto.getRelativeGap())
                .intervals(new Profile(taskDto.getIntervals()));

        mapDemand(taskDto, taskBuilder);
        mapProduction(taskDto, taskBuilder);
        mapContracts(taskDto, taskBuilder);
        mapStorages(taskDto, taskBuilder);
        mapMovableDemands(taskDto, taskBuilder);

        return taskBuilder.build();
    }

    private void mapDemand(TaskDto taskDto, Task.TaskBuilder taskBuilder) {
        DemandDto demandDto = taskDto.getDemand();
        taskBuilder.demand(new Demand(
                demandDto.getId(),
                demandDto.getName().toString(),
                new Profile(demandDto.getDemandProfile())
        ));
    }

    private void mapProduction(TaskDto taskDto, Task.TaskBuilder taskBuilder) {
        ProductionDto productionDto = taskDto.getProduction();
        taskBuilder.production(new Production(
                productionDto.getId(),
                productionDto.getName().toString(),
                new Profile(productionDto.getProductionProfile())
        ));
    }

    private void mapContracts(TaskDto taskDto, Task.TaskBuilder taskBuilder) {
        List<Object> contractDtoList = taskDto.getContracts();
        for (Object contractDtoObject : contractDtoList) {

            ContractDto contractDto = (ContractDto) contractDtoObject;
            Contract.ContractBuilder contractBuilder = new Contract.ContractBuilder(
                    contractDto.getId(),
                    contractDto.getName().toString(),
                    new Profile(contractDto.getStartInterval(), contractDto.getUnitPrice()),
                    contractDirectionMapper(contractDto.getContractDirection())
            );

            if (contractDto.getMinPower() != null) {
                contractDto.getMinPower().forEach(
                        (key, value) -> contractBuilder.minPowerConstraint(Integer.parseInt(key.toString()), value)
                );
            }
            if (contractDto.getMaxPower() != null) {
                contractDto.getMaxPower().forEach(
                        (key, value) -> contractBuilder.maxPowerConstraint(Integer.parseInt(key.toString()), value)
                );
            }

            if (contractDto.getMinEnergy() != null) {
                contractDto.getMinEnergy().forEach(
                        sumConstraintDto -> contractBuilder.minEnergyConstraint(
                                new SumConstraint(sumConstraintDto.getStartInterval(), sumConstraintDto.getEndInterval(), sumConstraintDto.getSum())
                        )
                );
            }
            if (contractDto.getMaxEnergy() != null) {
                contractDto.getMaxEnergy().forEach(
                        sumConstraintDto -> contractBuilder.maxEnergyConstraint(
                                new SumConstraint(sumConstraintDto.getStartInterval(), sumConstraintDto.getEndInterval(), sumConstraintDto.getSum())
                        )
                );
            }

            taskBuilder.contract(contractBuilder.build());
        }
    }

    private void mapStorages(TaskDto taskDto, Task.TaskBuilder taskBuilder) {
        List<Object> storagesDtoList = taskDto.getStorages();
        for (Object storageDtoObject : storagesDtoList) {

            StorageDto storageDto = (StorageDto) storageDtoObject;

            Storage.StorageBuilder storageBuilder = new Storage.StorageBuilder(
                    storageDto.getId(),
                    storageDto.getName().toString(),
                    storageDto.getMaxCharge(),
                    storageDto.getMaxDischarge(),
                    storageDto.getMaxCapacity()
            );

            storageBuilder.initialEnergy(storageDto.getInitialEnergy());

            if (storageDto.getMinChargeConstraints() != null) {
                storageDto.getMinChargeConstraints().forEach(
                        (key, value) -> storageBuilder.minChargeConstraint(Integer.parseInt(key.toString()), value)
                );
            }
            if (storageDto.getMaxChargeConstraints() != null) {
                storageDto.getMaxChargeConstraints().forEach(
                        (key, value) -> storageBuilder.maxChargeConstraint(Integer.parseInt(key.toString()), value)
                );
            }

            if (storageDto.getMinDischargeConstraints() != null) {
                storageDto.getMinDischargeConstraints().forEach(
                        (key, value) -> storageBuilder.minDischargeConstraint(Integer.parseInt(key.toString()), value)
                );
            }
            if (storageDto.getMaxDischargeConstraints() != null) {
                storageDto.getMaxDischargeConstraints().forEach(
                        (key, value) -> storageBuilder.maxDischargeConstraint(Integer.parseInt(key.toString()), value)
                );
            }

            if (storageDto.getMinEnergyConstraints() != null) {
                storageDto.getMinEnergyConstraints().forEach(
                        (key, value) -> storageBuilder.minEnergyConstraint(Integer.parseInt(key.toString()), value)
                );
            }
            if (storageDto.getMaxEnergyConstraints() != null) {
                storageDto.getMaxEnergyConstraints().forEach(
                        (key, value) -> storageBuilder.maxEnergyConstraint(Integer.parseInt(key.toString()), value)
                );
            }

            if (storageDto.getForbiddenChargeIntervals() != null) {
                storageBuilder.forbiddenChargeIntervals(new HashSet<>(storageDto.getForbiddenChargeIntervals()));
            }
            if (storageDto.getForbiddenDischargeIntervals() != null) {
                storageBuilder.forbiddenDischargeIntervals(new HashSet<>(storageDto.getForbiddenDischargeIntervals()));
            }

            taskBuilder.storage(storageBuilder.build());
        }
    }

    private void mapMovableDemands(TaskDto taskDto, Task.TaskBuilder taskBuilder) {
        List<Object> movableDemandDtoList = taskDto.getMovableDemands();
        for (Object movableDemantDtoObject : movableDemandDtoList) {

            MovableDemandDto movableDemandDto = (MovableDemandDto) movableDemantDtoObject;

            taskBuilder.movableDemand(new MovableDemand(
                    movableDemandDto.getId(),
                    movableDemandDto.getName().toString(),
                    movableDemandDto.getProfile(),
                    new HashSet<>(movableDemandDto.getStartIntervals())
            ));
        }
    }

    private ContractDirection contractDirectionMapper(ContractDirectionDto contractDirectionDto) {

        if (ContractDirectionDto.PURCHASE == contractDirectionDto) {
            return ContractDirection.PURCHASE;
        }
        else {
            return ContractDirection.SELL;
        }
    }
}
