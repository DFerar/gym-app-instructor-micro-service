package com.gym.gymmicroservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.entity.MonthEntity;
import com.gym.gymmicroservice.entity.YearEntity;
import com.gym.gymmicroservice.repository.WorkloadRepository;

import java.time.LocalDate;
import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;
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
        List<YearEntity> workload = new ArrayList<>();
        List<MonthEntity> monthWorkload = new ArrayList<>();
        monthWorkload.add(new MonthEntity(1, 5));
        workload.add(new YearEntity(2022, monthWorkload));
        entity.setWorkload(workload);

        when(workloadRepository.findByUsername(any())).thenReturn(entity);
        // When
        workloadCalculationService.addWorkload(entity, LocalDate.now(), 5, ActionType.ADD);
        // Then
        verify(workloadRepository, times(1)).save(any());
    }

    @Test
    void shouldSubtractWorkloadWhenInstructorExists() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        List<YearEntity> workload = new ArrayList<>();
        List<MonthEntity> monthWorkload = new ArrayList<>();
        monthWorkload.add(new MonthEntity(1, 5));
        workload.add(new YearEntity(2022, monthWorkload));
        entity.setWorkload(workload);

        when(workloadRepository.findByUsername(any())).thenReturn(entity);
        // When
        workloadCalculationService.addWorkload(entity, LocalDate.of(2022, 1, 1), 3, ActionType.DELETE);
        // Then
        verify(workloadRepository, times(1)).save(any());
        assertThat(entity.getWorkload().get(0).getMonths().get(0).getWorkload()).isEqualTo(2);
    }

    @Test
    void shouldThrowExceptionWhenInstructorExistsAndWorkloadIsNull() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        entity.setWorkload(null);

        when(workloadRepository.findByUsername(any())).thenReturn(entity);
        // When
        assertThatThrownBy(() -> workloadCalculationService.addWorkload(entity, LocalDate.now(), 5, ActionType.ADD))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenInstructorExistsAndYearIsNull() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        List<YearEntity> workload = new ArrayList<>();
        List<MonthEntity> monthWorkload = new ArrayList<>();
        monthWorkload.add(new MonthEntity(1, 5));
        workload.add(new YearEntity(null, monthWorkload));
        entity.setWorkload(workload);

        when(workloadRepository.findByUsername(any())).thenReturn(entity);
        // When
        assertThatThrownBy(() -> workloadCalculationService.addWorkload(entity, LocalDate.now(), 5, ActionType.ADD))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldThrowExceptionWhenDurationBecomesNegative() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        List<YearEntity> workload = new ArrayList<>();
        List<MonthEntity> monthWorkload = new ArrayList<>();
        monthWorkload.add(new MonthEntity(1, 5));
        workload.add(new YearEntity(2022, monthWorkload));
        entity.setWorkload(workload);
        // When
        assertThatThrownBy(() -> workloadCalculationService.addWorkload(entity, LocalDate.now(), 6, ActionType.DELETE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowExceptionWhenTypeIsDeleteAndWorkloadIsNull() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        entity.setWorkload(null);
        // When
        assertThatThrownBy(() -> workloadCalculationService.addWorkload(entity, LocalDate.now(), 5, ActionType.DELETE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldUpdateWorkloadIfMonthAlreadyExists() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        List<YearEntity> workload = new ArrayList<>();
        List<MonthEntity> monthWorkload = new ArrayList<>();
        monthWorkload.add(new MonthEntity(1, 5));
        workload.add(new YearEntity(2022, monthWorkload));
        entity.setWorkload(workload);

        when(workloadRepository.findByUsername(any())).thenReturn(entity);
        // When
        workloadCalculationService.addWorkload(entity, LocalDate.of(2022, 1, 3), 5, ActionType.ADD);
        // Then
        verify(workloadRepository, times(1)).save(any());
        assertThat(entity.getWorkload().get(0).getMonths().get(0).getWorkload()).isEqualTo(10);
    }

    @Test
    void shouldCreateNewMonthWhenYearExistsAndMonthNot() {
        // Given
        InstructorWorkloadEntity entity = new InstructorWorkloadEntity();
        entity.setUsername(RandomStringUtils.randomAlphabetic(10));
        List<YearEntity> workload = new ArrayList<>();
        List<MonthEntity> monthWorkload = new ArrayList<>();
        monthWorkload.add(new MonthEntity(1, 5));
        workload.add(new YearEntity(2022, monthWorkload));
        entity.setWorkload(workload);

        when(workloadRepository.findByUsername(any())).thenReturn(entity);
        // When
        workloadCalculationService.addWorkload(entity, LocalDate.of(2022, 2, 3), 5, ActionType.ADD);
        // Then
        verify(workloadRepository, times(1)).save(any());
        assertThat(entity.getWorkload().get(0).getMonths().size()).isEqualTo(2);
    }
}