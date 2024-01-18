package com.github.kacperpotapczyk.pvoptimizer.optimizer;

import com.github.kacperpotapczyk.pvoptimizer.avro.optimizer.task.*;
import com.github.kacperpotapczyk.pvoptimizer.avro.optimizer.result.*;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.kafka.KafkaMockupProducer;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.kafka.KafkaMockupProducerConfig;
import com.github.kacperpotapczyk.pvoptimizer.optimizer.kafka.KafkaTestResultConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({KafkaMockupProducerConfig.class, KafkaTestResultConsumer.class})
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:3333",
                "port=3333"})
public class EndToEndTests {

    private final KafkaTemplate<String, TaskDto> kafkaTemplate;
    private final KafkaTestResultConsumer kafkaTestResultConsumer;

    @Autowired
    public EndToEndTests(
            @Qualifier("mockupKafkaTemplate") KafkaTemplate<String, TaskDto> kafkaTemplate,
            KafkaTestResultConsumer kafkaTestResultConsumer) {

        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTestResultConsumer = kafkaTestResultConsumer;
    }

    @Test
    public void endToEndTest() throws InterruptedException {

        List<Double> intervals = Arrays.asList(1.0, 1.0, 1.0);
        List<Double> demandProfile = Arrays.asList(1.0, 1.0, 1.0);
        List<Double> productionProfile = Arrays.asList(1.0, 1.0, 1.0);

        TaskDto taskDto1 = getTaskDto1(intervals, demandProfile, productionProfile);
        TaskDto taskDto2 = getTaskDto2(intervals, demandProfile, productionProfile);

        CountDownLatch producerCountDown = new CountDownLatch(2);
        KafkaMockupProducer kafkaMockupProducer = new KafkaMockupProducer(kafkaTemplate, producerCountDown);

        kafkaMockupProducer.send("pvoptimizer","1", taskDto1);
        kafkaMockupProducer.send("pvoptimizer","2", taskDto2);
        assertTrue(producerCountDown.await(1000L, TimeUnit.MILLISECONDS));

        // Adjust CountDownLatch in kafkaTestResultConsumer to match number of tasks
        assertTrue(kafkaTestResultConsumer.getCountDownLatch().await(10000L, TimeUnit.MILLISECONDS));
        assertEquals(2, kafkaTestResultConsumer.getResultDtoList().size());

        resultAssertion(kafkaTestResultConsumer.getResultDtoList());
    }

    private void resultAssertion(List<ResultDto> resultDtoList) {

        ResultDto resultDto1 = resultDtoList.stream()
                .filter(resultDto -> resultDto.getId() == 1L)
                .findFirst().orElseThrow();

        assertEquals(0.0, resultDto1.getObjectiveFunctionValue(), 1e-6);
        assertEquals(OptimizationStatusDto.SOLUTION_FOUND, resultDto1.getOptimizationStatus());
        assertEquals(0.0, resultDto1.getContractResults().get(0).getEnergy().get(0), 1e-6);

        ResultDto resultDto2 = resultDtoList.stream()
                .filter(resultDto -> resultDto.getId() == 2L)
                .findFirst().orElseThrow();

        assertEquals(-71.0, resultDto2.getObjectiveFunctionValue(), 1e-6);
        assertEquals(OptimizationStatusDto.SOLUTION_FOUND, resultDto2.getOptimizationStatus());
        assertEquals(2, resultDto2.getMovableDemandResults().get(0).getStartInterval());
        assertEquals(1.0, resultDto2.getStorageResults().get(0).getEnergy().get(0), 1e-6);
        assertEquals(10.0, resultDto2.getStorageResults().get(0).getEnergy().get(1), 1e-6);
        assertEquals(0.0, resultDto2.getStorageResults().get(0).getEnergy().get(2), 1e-6);

        ContractResultDto purchaseResult = resultDto2.getContractResults().stream()
                .filter(contractResultDto -> contractResultDto.getId() == 1L)
                .findFirst().orElseThrow();

        assertEquals(9.0, purchaseResult.getEnergy().get(0), 1e-6);
        assertEquals(0.0, purchaseResult.getEnergy().get(1), 1e-6);
        assertEquals(9.0, purchaseResult.getCost().get(0), 1e-6);
    }

    private TaskDto getTaskDto2(List<Double> intervals, List<Double> demandProfile, List<Double> productionProfile) {

        TaskDto.Builder taskDtoBuilder = getBaseTaskBuilder(2L, intervals, demandProfile, productionProfile);

        ContractDto purchaseContract = ContractDto.newBuilder()
                .setId(1L)
                .setName("purchase")
                .setContractDirection(ContractDirectionDto.PURCHASE)
                .setStartInterval(1)
                .setUnitPrice(Arrays.asList(1.0, 1e3))
                .build();

        ContractDto sellContract = ContractDto.newBuilder()
                .setId(2L)
                .setName("sell")
                .setContractDirection(ContractDirectionDto.SELL)
                .setStartInterval(2)
                .setUnitPrice(List.of(10.0))
                .setMaxEnergy(List.of(new SumConstraintDto(2, 2, 10.0)))
                .build();

        StorageDto storage = StorageDto.newBuilder()
                .setId(10L)
                .setName("storage")
                .setInitialEnergy(1.0)
                .setMaxCharge(10.0)
                .setMaxDischarge(10.0)
                .setMaxCapacity(20.0)
                .build();

        MovableDemandDto movableDemand = MovableDemandDto.newBuilder()
                .setId(44L)
                .setName("movableDemand")
                .setStartIntervals(Arrays.asList(0, 2))
                .setProfile(List.of(2.0))
                .build();

        taskDtoBuilder
                .setContracts(Arrays.asList(purchaseContract, sellContract))
                .setStorages(List.of(storage))
                .setMovableDemands(List.of(movableDemand));

        return taskDtoBuilder.build();
    }

    private TaskDto getTaskDto1(List<Double> intervals, List<Double> demandProfile, List<Double> productionProfile) {

        TaskDto.Builder taskDtoBuilder = getBaseTaskBuilder(1L, intervals, demandProfile, productionProfile);

        ContractDto contractDto = ContractDto.newBuilder()
                .setId(8081L)
                .setName("purchase balancing contract")
                .setContractDirection(ContractDirectionDto.PURCHASE)
                .setUnitPrice(Arrays.asList(1e3, 1e3))
                .build();

        taskDtoBuilder.setContracts(List.of(contractDto));
        return taskDtoBuilder.build();
    }

    private TaskDto.Builder getBaseTaskBuilder(long taskId, List<Double> intervals, List<Double> demandProfile, List<Double> productionProfile) {

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
