package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.Task;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.consumer.topic}")
    public void listener(@Payload ConsumerRecord<String, Task> consumerRecord, Acknowledgment acknowledgment) {
        System.out.println("Received task with id: " + consumerRecord.value().getId());
        System.out.println("Received task with timeout: " + consumerRecord.value().getTimeoutSeconds());
        System.out.println("Message: " + consumerRecord.value().toString());
        acknowledgment.acknowledge();
    }
}
