package com.github.kacperpotapczyk.pvoptimizer.kafka;

import com.github.kacperpotapczyk.pvoptimizer.dto.ResultDto;
import com.github.kacperpotapczyk.pvoptimizer.dto.TaskDto;
import com.github.kacperpotapczyk.pvoptimizer.model.Result;
import com.github.kacperpotapczyk.pvoptimizer.model.Task;
import com.github.kacperpotapczyk.pvoptimizer.service.Mapper;
import com.github.kacperpotapczyk.pvoptimizer.service.Optimizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    @Value("${spring.kafka.consumer.containerId}")
    private String containerId;

    private final KafkaProducer kafkaProducer;
    private final Optimizer optimizer;
    private final Mapper<TaskDto, Task> taskToDtoTaskMapper;
    private final Mapper<Result, ResultDto> resultToResultDtoMapper;
    private final KafkaListenerEndpointRegistry registry;
    private final AsyncTaskExecutor executor;

    @KafkaListener(topics = "${spring.kafka.consumer.topic}", id = "${spring.kafka.consumer.containerId}")
    public void listener(@Payload ConsumerRecord<String, TaskDto> consumerRecord, Acknowledgment acknowledgment) {

        log.info("Received record with partition={}, offset={}, key={}", consumerRecord.partition(), consumerRecord.offset(), consumerRecord.key());
        TaskDto taskDto = consumerRecord.value();
        log.info("Record contains task with id={}", taskDto.getId());

        getContainer(containerId).ifPresent(MessageListenerContainer::pause);
        log.debug("Pausing container={}", getContainer(containerId));

        executor.submitCompletable(() -> {
            Task task = taskToDtoTaskMapper.map(taskDto);
            Result result = optimizer.solve(task);
            ResultDto resultDto = resultToResultDtoMapper.map(result);
            kafkaProducer.send(consumerRecord.key(), resultDto);
        }).thenAccept(result -> {
            acknowledgment.acknowledge();
            getContainer(containerId).ifPresent(MessageListenerContainer::resume);
            log.debug("Resume container");
        });
    }

    private Optional<MessageListenerContainer> getContainer(String containerId) {
        return Optional.ofNullable(registry.getListenerContainer(containerId));
    }
}
