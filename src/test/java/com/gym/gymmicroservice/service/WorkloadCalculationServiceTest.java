package com.gym.gymmicroservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import java.util.NoSuchElementException;
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

    @Test
    void shouldAddDurationWhenActionTypeIsAddAndCurrentDurationIsNotNull() {
        // Given
        int year = 2022;
        int month = 1;
        int duration = 5;
        ActionType actionType = ActionType.ADD;
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        monthWorkload.put(month, duration);
        workload.put(year, monthWorkload);
        // When
        workloadCalculationService.processWorkload(duration, actionType, workload, year, month);
        // Then
        assertThat(workload.get(year).get(month)).isEqualTo(duration * 2);
    }

    @Test
    void shouldSubtractDurationWhenActionTypeIsDeleteAndCurrentDurationIsNotNull() {
        // Given
        int year = 2022;
        int month = 1;
        int duration = 5;
        ActionType actionType = ActionType.DELETE;
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        monthWorkload.put(month, duration * 2);
        workload.put(year, monthWorkload);
        // When
        workloadCalculationService.processWorkload(duration, actionType, workload, year, month);
        // Then
        assertThat(workload.get(year).get(month)).isEqualTo(duration);
    }

    @Test
    void shouldRemoveMonthWhenDurationBecomesZeroAfterDeletionAndCurrentDurationIsNotNull() {
        // Given
        int year = 2022;
        int month = 1;
        int duration = 5;
        ActionType actionType = ActionType.DELETE;
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        monthWorkload.put(month, duration);
        workload.put(year, monthWorkload);
        // When
        workloadCalculationService.processWorkload(duration, actionType, workload, year, month);
        // Then
        assertThat(workload.get(year)).doesNotContainKey(month);
    }

    @Test
    void shouldAddNewMonthWhenMonthDoesNotExistAndCurrentDurationIsNull() {
        // Given
        int year = 2022;
        int month = 1;
        int duration = 5;
        ActionType actionType = ActionType.ADD;
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        workload.put(year, monthWorkload);
        // When
        workloadCalculationService.processWorkload(duration, actionType, workload, year, month);
        // Then
        assertThat(workload.get(year).get(month)).isEqualTo(duration);
    }

    @Test
    void shouldAddNewYearWhenYearDoesNotExistAndCurrentDurationIsNull() {
        // Given
        int year = 2023;
        int month = 1;
        int duration = 5;
        ActionType actionType = ActionType.ADD;
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        workload.put(year - 1, monthWorkload);
        // When
        workloadCalculationService.processWorkload(duration, actionType, workload, year, month);
        // Then
        assertThat(workload).containsKey(year);
        assertThat(workload.get(year).get(month)).isEqualTo(duration);
    }

    @Test
    void shouldAddDurationWhenActionTypeIsAddAndCurrentDurationIsNull() {
        //Given
        int year = 2022;
        int month = 1;
        int duration = 5;
        ActionType actionType = ActionType.ADD;
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        workload.put(year, monthWorkload);
        //When
        workloadCalculationService.processWorkload(duration, actionType, workload, year, month);
        //Then
        assertThat(workload.get(year).get(month)).isEqualTo(duration);
    }

    @Test
    void shouldThrowExceptionWhenActionTypeIsDeleteAndCurrentDurationIsNull() {
        //Given
        int year = 2022;
        int month = 1;
        int duration = 5;
        ActionType actionType = ActionType.DELETE;
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        workload.put(year, monthWorkload);
        //When & Then
        assertThatThrownBy(
            () -> workloadCalculationService.processWorkload(duration, actionType, workload, year, month)).isInstanceOf(
                NoSuchElementException.class)
            .hasMessageContaining("Month not found in workload");
    }
}