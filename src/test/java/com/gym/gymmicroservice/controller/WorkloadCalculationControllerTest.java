package com.gym.gymmicroservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.mapper.AcceptWorkloadMapper;
import com.gym.gymmicroservice.service.WorkloadCalculationService;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkloadCalculationControllerTest {

    @Mock
    private WorkloadCalculationService workloadCalculationService;

    @Mock
    private AcceptWorkloadMapper acceptWorkloadMapper;

    @InjectMocks
    private WorkloadCalculationController workloadCalculationController;


    @Test
    void shouldAcceptWorkloadWhenValidRequestIsProvided() {
        // Given
        AcceptWorkloadRequestDto requestDto = new AcceptWorkloadRequestDto();
        requestDto.setTrainingDate(LocalDate.now());
        requestDto.setTrainingDuration(5);
        requestDto.setActionType(ActionType.ADD);
        // When
        workloadCalculationController.acceptWorkload(requestDto);
        // Then
        verify(acceptWorkloadMapper, times(1)).dtoToEntity(any());
        verify(workloadCalculationService, times(1)).addWorkload(any(), any(), anyInt(), any());
    }
}