package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.Task;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaMockupProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaTemplate<String, Task> kafkaTemplate(final ProducerFactory<String, Task> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, Task> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(), StringSerializer::new, SpecificAvroSerializer<Task>::new);
    }
}
