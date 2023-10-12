package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Getter
@TestComponent
@RequiredArgsConstructor
public class KafkaTestResultConsumer {

    private final List<ResultDto> resultDtoList = new ArrayList<>();
    private final CountDownLatch countDownLatch = new CountDownLatch(2);

    @KafkaListener(topics = "${spring.kafka.producer.topic}")
    public void listener(@Payload ConsumerRecord<String, ResultDto> consumerRecord) {

        ResultDto resultDto = consumerRecord.value();
        System.out.println("Received results: " + resultDto);
        resultDtoList.add(resultDto);
        countDownLatch.countDown();
    }
}
