package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.OptimizationStatusDto;
import com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto;
import com.github.kacperpotapczyk.pvoptimizer.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final KafkaProducer kafkaProducer;

    @KafkaListener(topics = "${spring.kafka.consumer.topic}")
    public void listener(@Payload ConsumerRecord<String, TaskDto> consumerRecord, Acknowledgment acknowledgment) {

        TaskDto taskDto = consumerRecord.value();
        long id = taskDto.getId();
        System.out.println("Received task with id: " + id);

        ResultDto resultDto = ResultDto.newBuilder()
                .setId(id)
                .setOptimizationStatus(OptimizationStatusDto.SOLUTION_FOUND)
                .setObjectiveFunctionValue(42)
                .setElapsedTime(10.0)
                .setRelativeGap(0.0)
                .build();

        kafkaProducer.send(consumerRecord.key(), resultDto);

        acknowledgment.acknowledge();
    }
}
