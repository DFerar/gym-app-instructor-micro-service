package com.gym.gymmicroservice.integration.common;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.repository.WorkloadRepository;
import com.gym.gymmicroservice.service.KafkaConsumerService;
import java.time.LocalDate;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
public class WorkloadCalculationTest extends BaseItTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaConsumerService kafkaConsumerService;
    @Autowired
    private WorkloadRepository workloadRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void workloadCalculationTest() throws JsonProcessingException {
        //Given
        AcceptWorkloadRequestDto request = new AcceptWorkloadRequestDto();
        request.setUsername(RandomStringUtils.randomAlphabetic(5));
        request.setFirstName(RandomStringUtils.randomAlphabetic(5));
        request.setLastName(RandomStringUtils.randomAlphabetic(5));
        request.setTrainingDate(LocalDate.of(2021, 1, 1));
        request.setTrainingDuration(2);
        request.setIsActive(true);
        request.setActionType(ActionType.ADD);
        String message = objectMapper.writeValueAsString(request);

        kafkaTemplate.send("gym-topic", message);
        //When
        kafkaConsumerService.consume(message);
        //Then
        InstructorWorkloadEntity entity = workloadRepository.findByUsername(request.getUsername());

        assertThat(entity).isNotNull();
        assertThat(entity.getWorkload().size()).isEqualTo(1);
        assertThat(entity.getWorkload().get(0).getMonths().get(0).getWorkload()).isEqualTo(2);
        assertThat(entity.getWorkload().get(0).getMonths().get(0).getMonth()).isEqualTo(1);
        assertThat(entity.getWorkload().get(0).getYear()).isEqualTo(2021);
    }
}
