package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducer {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    private final KafkaTemplate<String, ResultDto> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, ResultDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String key, ResultDto resultDto) {

        kafkaTemplate.send(topic, key, resultDto);
        kafkaTemplate.flush();
        log.info("Record with key={} containing result of task with id={} was send", key, resultDto.getId());
    }
}
