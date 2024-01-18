package com.github.kacperpotapczyk.pvoptimizer.optimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.avro.optimizer.result.ResultDto;
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
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public KafkaTemplate<String, ResultDto> kafkaTemplate(final ProducerFactory<String, ResultDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, ResultDto> producerFactory() {
        return new DefaultKafkaProducerFactory<>(kafkaProperties.buildProducerProperties(), StringSerializer::new, SpecificAvroSerializer<ResultDto>::new);
    }
}
