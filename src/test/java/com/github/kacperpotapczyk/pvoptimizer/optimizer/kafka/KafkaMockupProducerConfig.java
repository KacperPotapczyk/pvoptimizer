package com.github.kacperpotapczyk.pvoptimizer.optimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.avro.optimizer.task.TaskDto;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@TestConfiguration
@RequiredArgsConstructor
public class KafkaMockupProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean("mockupKafkaTemplate")
    public KafkaTemplate<String, TaskDto> kafkaTemplate(@Qualifier("mockupProducerFactory") final ProducerFactory<String, TaskDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean("mockupProducerFactory")
    public ProducerFactory<String, TaskDto> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(), StringSerializer::new, SpecificAvroSerializer<TaskDto>::new);
    }
}
