package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@TestConfiguration
@RequiredArgsConstructor
public class KafkaTestResultConsumer {

    private final List<ResultDto> resultDtoList = new ArrayList<>();

    @KafkaListener(topics = "${spring.kafka.producer.topic}")
    public void listener(@Payload ConsumerRecord<String, ResultDto> consumerRecord) {

        ResultDto resultDto = consumerRecord.value();
        System.out.println("Received results: " + resultDto);
        resultDtoList.add(resultDto);
    }

    public Optional<ResultDto> getResultById(long id) {
        return resultDtoList.stream()
                .filter(resultDto -> resultDto.getId() == id)
                .findFirst();
    }
}
