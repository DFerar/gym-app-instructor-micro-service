package com.gym.gymmicroservice.service;

import static com.gym.gymmicroservice.dto.request.ActionType.ADD;
import static com.gym.gymmicroservice.dto.request.ActionType.DELETE;

import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.repository.WorkloadRepository;
import java.time.LocalDate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadCalculationService {
    private final WorkloadRepository workloadRepository;

    public void addWorkload(InstructorWorkloadEntity entity, LocalDate trainingDate, int duration,
                            ActionType actionType) {
        InstructorWorkloadEntity existingEntity = workloadRepository.findByUsername(entity.getUsername());
        if (existingEntity != null) {
            var workload = getWorkload(existingEntity, trainingDate, duration, actionType);

            existingEntity.setWorkload(workload);
            workloadRepository.save(existingEntity);
        } else {
            Map<Integer, Map<Integer, Integer>> workload = Map.of(trainingDate.getYear(),
                Map.of(trainingDate.getMonthValue(),
                    duration));
            entity.setWorkload(workload);
            workloadRepository.save(entity);
        }
    }

    private Map<Integer, Map<Integer, Integer>> getWorkload(InstructorWorkloadEntity existingEntity,
                                                            LocalDate trainingDate, int duration,
                                                            ActionType actionType) {
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();
        Map<Integer, Map<Integer, Integer>> workload = existingEntity.getWorkload();
        if (workload.containsKey(year)) {
            processWorkload(duration, actionType, workload, year, month);
        } else {
            createNewWorkload(duration, month, workload, year);
        }
        return workload;
    }

    private void createNewWorkload(int duration, int month, Map<Integer, Map<Integer, Integer>> workload, int year) {
        Map<Integer, Integer> monthWorkload = Map.of(month, duration);
        workload.put(year, monthWorkload);
    }

    private void processWorkload(int duration, ActionType actionType, Map<Integer, Map<Integer, Integer>> workload,
                                 int year, int month) {
        Map<Integer, Integer> monthWorkload = workload.get(year);
        if (monthWorkload.containsKey(month) && actionType == ADD) {
            monthWorkload.put(month, monthWorkload.get(month) + duration);
        } else if (monthWorkload.containsKey(month) && actionType == DELETE) {
            monthWorkload.put(month, monthWorkload.get(month) - duration);
        } else { //TODO
            monthWorkload.put(month, duration);
        }
    }
}
