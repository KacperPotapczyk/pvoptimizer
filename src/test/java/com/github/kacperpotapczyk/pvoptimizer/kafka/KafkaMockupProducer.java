package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public class KafkaMockupProducer {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger("unitTest");
    private final KafkaTemplate<String, TaskDto> kafkaTemplate;
    private final CountDownLatch countDownLatch;

    public void send(String topic, String key, TaskDto task) {
        kafkaTemplate.send(topic, key, task);
        kafkaTemplate.flush();
        log.debug("Sending message={}", task.toString());
        countDownLatch.countDown();
    }
}
