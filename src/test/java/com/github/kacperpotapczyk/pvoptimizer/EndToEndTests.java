package com.github.kacperpotapczyk.pvoptimizer;

import com.github.kacperpotapczyk.pvoptimizer.dto.*;
import com.github.kacperpotapczyk.pvoptimizer.kafka.KafkaMockupProducer;
import com.github.kacperpotapczyk.pvoptimizer.kafka.KafkaMockupProducerConfig;
import com.github.kacperpotapczyk.pvoptimizer.kafka.KafkaTestResultConsumer;
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
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"})
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
    public void mockupProducerWorks() throws InterruptedException {

        List<Double> intervals = Arrays.asList(1.0, 1.0);
        List<Double> demandProfile = Arrays.asList(0.0, 0.0);
        List<Double> productionProfile = Arrays.asList(0.0, 0.0);
        List<Double> unitPrice = Arrays.asList(10.0, 10.0);

        ContractDto contract = ContractDto.newBuilder()
                .setId(10L)
                .setName("Test")
                .setContractDirection(ContractDirectionDto.PURCHASE)
                .setUnitPrice(unitPrice)
                .build();

        List<Object> contracts = new ArrayList<>();
        contracts.add(contract);

        DemandDto demand = DemandDto.newBuilder()
                .setId(2L)
                .setName("Test demand")
                .setDemandProfile(demandProfile)
                .build();

        ProductionDto production = ProductionDto.newBuilder()
                .setId(2L)
                .setName("Test production")
                .setProductionProfile(productionProfile)
                .build();

        TaskDto task = TaskDto.newBuilder()
                .setId(1L)
                .setTimeoutSeconds(300L)
                .setIntervals(intervals)
                .setDemand(demand)
                .setProduction(production)
                .setContracts(contracts)
                .setStorages(Collections.emptyList())
                .setMovableDemands(Collections.emptyList())
                .build();

        CountDownLatch producerCountDown = new CountDownLatch(1);
        KafkaMockupProducer kafkaMockupProducer = new KafkaMockupProducer(kafkaTemplate, producerCountDown);
        kafkaMockupProducer.send("pvoptimizer","1", task);
        assertTrue(producerCountDown.await(1000L, TimeUnit.MILLISECONDS));

        Thread.sleep(1000L);
        assertEquals(1, kafkaTestResultConsumer.getResultDtoList().size());

        Optional<ResultDto> resultDto = kafkaTestResultConsumer.getResultById(1L);
        assertTrue(resultDto.isPresent());
        assertEquals(42.0, resultDto.get().getObjectiveFunctionValue());
        assertEquals(10.0, resultDto.get().getElapsedTime());
    }
}
