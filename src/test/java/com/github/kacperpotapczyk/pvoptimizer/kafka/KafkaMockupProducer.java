package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public class KafkaMockupProducer {


    private final KafkaTemplate<String, Task> kafkaTemplate;
    private final CountDownLatch countDownLatch;

    public void send(String topic, String key, Task task) {
        kafkaTemplate.send(topic, key, task);
        kafkaTemplate.flush();
        System.out.println("Sending message: " + task.toString());
        countDownLatch.countDown();
    }
}
