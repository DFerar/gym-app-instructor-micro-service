package com.gym.gymmicroservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.mapper.AcceptWorkloadMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final AcceptWorkloadMapper acceptWorkloadMapper;
    private final WorkloadCalculationService workloadCalculationService;
    private final ObjectMapper objectMapper;

    /**
     * This method is annotated with @KafkaListener, which makes it a listener for a Kafka topic.
     * It consumes messages from the "gym-topic" Kafka topic and processes them.
     *
     * @param request The incoming message from the Kafka topic. It's a string that represents an AcceptWorkloadRequestDto object.
     */
    @KafkaListener(topics = "gym-topic", groupId = "gym-group")
    public void consume(String request) {
        var dto = convertStringToObject(request);
        var entity = acceptWorkloadMapper.dtoToEntity(dto);
        workloadCalculationService.addWorkload(entity, dto.getTrainingDate(), dto.getTrainingDuration(),
            dto.getActionType());
        log.info("Consumed message: {}", request);
    }

    @SneakyThrows
    private AcceptWorkloadRequestDto convertStringToObject(String request) {
        return objectMapper.readValue(request, AcceptWorkloadRequestDto.class);
    }
}
