package com.gym.gymmicroservice.service;

import static com.gym.gymmicroservice.dto.request.ActionType.ADD;
import static com.gym.gymmicroservice.dto.request.ActionType.DELETE;

import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.repository.WorkloadRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkloadCalculationService {
    private final WorkloadRepository workloadRepository;

    /**
     * This method is used to add a workload to an instructor. It first checks if the instructor already exists in the repository.
     * If the instructor exists, it updates the instructor's workload. If the instructor does not exist, it creates a new workload for the instructor.
     *
     * @param entity The InstructorWorkloadEntity to which the workload is to be added.
     * @param trainingDate The date of the training.
     * @param duration The duration of the training.
     * @param actionType The type of action to be performed (ADD or DELETE).
     */
    public void addWorkload(InstructorWorkloadEntity entity, LocalDate trainingDate, int duration,
                            ActionType actionType) {
        InstructorWorkloadEntity existingEntity = workloadRepository.findByUsername(entity.getUsername());
        if (existingEntity != null) {
            var workload = getWorkload(existingEntity, trainingDate, duration, actionType);
            existingEntity.setWorkload(workload);
            log.info("Updating workload for instructor: {}", existingEntity);
            workloadRepository.save(existingEntity);
        } else {
            Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
            Map<Integer, Integer> monthWorkload = new HashMap<>();
            monthWorkload.put(trainingDate.getMonthValue(), duration);
            workload.put(trainingDate.getYear(), monthWorkload);
            entity.setWorkload(workload);
            log.info("Creating workload for instructor: {}", entity);
            workloadRepository.save(entity);
        }
    }

    /**
     * This method is used to get the workload of an instructor. It checks if the workload for the given year already exists.
     * If it does, it processes the workload. If it does not, it creates a new workload.
     *
     * @param existingEntity The existing InstructorWorkloadEntity.
     * @param trainingDate The date of the training.
     * @param duration The duration of the training.
     * @param actionType The type of action to be performed (ADD or DELETE).
     * @return The workload of the instructor.
     */
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

    /**
     * This method is used to create a new workload for a given month and year.
     *
     * @param duration The duration of the training.
     * @param month The month of the training.
     * @param workload The workload map to which the new workload is to be added.
     * @param year The year of the training.
     */
    private void createNewWorkload(int duration, int month, Map<Integer, Map<Integer, Integer>> workload, int year) {
        Map<Integer, Integer> monthWorkload = Map.of(month, duration);
        workload.put(year, monthWorkload);
    }

    /**
     * This method is used to process the workload of an instructor. It checks the action type and performs the corresponding action.
     * If the action type is ADD, it adds the duration to the existing workload. If the action type is DELETE, it subtracts the duration from the existing workload.
     * If the workload for the month becomes 0 after deletion, it removes the month from the workload.
     *
     * @param duration The duration of the training.
     * @param actionType The type of action to be performed (ADD or DELETE).
     * @param workload The workload map to be processed.
     * @param year The year of the training.
     * @param month The month of the training.
     */
    private void processWorkload(int duration, ActionType actionType, Map<Integer, Map<Integer, Integer>> workload,
                                 int year, int month) {
        Map<Integer, Integer> monthWorkload = workload.get(year);
        Integer currentDuration = monthWorkload.get(month);
        if (currentDuration != null) {
            if (actionType == ADD) {
                monthWorkload.put(month, currentDuration + duration);
            } else if (actionType == DELETE) {
                monthWorkload.put(month, currentDuration - duration);
                if (monthWorkload.get(month) == 0) {
                    monthWorkload.remove(month);
                }
            } else if (!workload.containsKey(month)) {
                monthWorkload.put(month, duration);
            }
        }
    }
}
