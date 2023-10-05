package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.TaskDto;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroDeserializer;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@EnableKafka
@Configuration
@AllArgsConstructor
public class KafkaConsumerConfig {

    private KafkaProperties kafkaProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskDto> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, TaskDto>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TaskDto> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), StringDeserializer::new, SpecificAvroDeserializer<TaskDto>::new);
    }
}
