package com.gym.gymmicroservice.controller;

import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.mapper.AcceptWorkloadMapper;
import com.gym.gymmicroservice.service.WorkloadCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workload")
public class WorkloadCalculationController {
    private final WorkloadCalculationService workloadCalculationService;
    private final AcceptWorkloadMapper acceptWorkloadMapper;

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptWorkload(@RequestBody AcceptWorkloadRequestDto requestDto) {
        InstructorWorkloadEntity entity = acceptWorkloadMapper.dtoToEntity(requestDto);
        workloadCalculationService.addWorkload(entity, requestDto.getTrainingDate(),
            requestDto.getTrainingDuration(), requestDto.getActionType());
        return ResponseEntity.ok().build();
    }
}
