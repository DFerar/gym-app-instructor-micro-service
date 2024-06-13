package com.gym.gymmicroservice.service;

import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.mapper.AcceptWorkloadMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final AcceptWorkloadMapper acceptWorkloadMapper;
    private final WorkloadCalculationService workloadCalculationService;

    @KafkaListener(topics = "gym-topic", groupId = "gym-group")
    public void consume(Map<Object, Object> map) {
//        var entity = acceptWorkloadMapper.dtoToEntity(dto);
//        workloadCalculationService.addWorkload(entity, dto.getTrainingDate(), dto.getTrainingDuration(),
//            dto.getActionType());
        log.info("Consumed message: {}", map);
    }
}
