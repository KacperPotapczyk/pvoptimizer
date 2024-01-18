package com.github.kacperpotapczyk.pvoptimizer.optimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.avro.optimizer.result.ResultDto;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@EnableKafka
@TestConfiguration
@AllArgsConstructor
public class KafkaTestResultConsumerConfig {

    private KafkaProperties kafkaProperties;

    @Bean("testContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ResultDto> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, ResultDto>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean("testConsumerFactory")
    public ConsumerFactory<String, ResultDto> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), StringDeserializer::new, SpecificAvroDeserializer<ResultDto>::new);
    }
}
