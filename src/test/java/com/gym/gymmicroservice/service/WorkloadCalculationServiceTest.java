package com.gym.gymmicroservice.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.repository.WorkloadRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkloadCalculationServiceTest {

    @Mock
    private WorkloadRepository workloadRepository;

    @InjectMocks
    private WorkloadCalculationService workloadCalculationService;

    @Test
    void shouldAddNewWorkloadWhenInstructorDoesNotExist() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));

        when(workloadRepository.findByUsername(any())).thenReturn(null);
        // When
        workloadCalculationService.addWorkload(entity, LocalDate.now(), 5, ActionType.ADD);
        // Then
        verify(workloadRepository, times(1)).save(any());
    }

    @Test
    void shouldUpdateExistingWorkloadWhenInstructorExists() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        monthWorkload.put(1, 5);
        workload.put(2022, monthWorkload);
        entity.setWorkload(workload);

        when(workloadRepository.findByUsername(any())).thenReturn(entity);
        // When
        workloadCalculationService.addWorkload(entity, LocalDate.now(), 5, ActionType.ADD);
        // Then
        verify(workloadRepository, times(1)).save(any());
    }
}