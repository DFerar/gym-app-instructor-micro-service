package com.gym.gymmicroservice.service;

import static com.gym.gymmicroservice.dto.request.ActionType.ADD;
import static com.gym.gymmicroservice.dto.request.ActionType.DELETE;

import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.repository.WorkloadRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
     * @param entity       The InstructorWorkloadEntity to which the workload is to be added.
     * @param trainingDate The date of the training.
     * @param duration     The duration of the training.
     * @param actionType   The type of action to be performed (ADD or DELETE).
     */
    public void addWorkload(InstructorWorkloadEntity entity, LocalDate trainingDate, int duration,
                            ActionType actionType) {
        InstructorWorkloadEntity existingEntity = workloadRepository.findByUsername(entity.getUsername());
        Map<Integer, Map<Integer, Integer>> workload;
        if (existingEntity != null) {
            workload = updateWorkload(entity, trainingDate, duration, actionType);
            entity.setWorkload(workload);
            log.info("Updating workload for instructor: {}", entity);
        } else {
            workload = createNewWorkloadForMonthAndYear(trainingDate, duration);
            entity.setWorkload(workload);
            log.info("Creating workload for instructor: {}", entity);
        }
        workloadRepository.save(entity);
    }

    /**
     * This method is used to process the workload of an instructor. It checks the action type and performs the corresponding action.
     * If the action type is ADD, it adds the duration to the existing workload. If the action type is DELETE, it subtracts the duration from the existing workload.
     * If the workload for the month becomes 0 after deletion, it removes the month from the workload.
     *
     * @param duration   The duration of the training.
     * @param actionType The type of action to be performed (ADD or DELETE).
     * @param workload   The workload map to be processed.
     * @param year       The year of the training.
     * @param month      The month of the training.
     */
    public void processWorkload(int duration, ActionType actionType, Map<Integer, Map<Integer, Integer>> workload,
                                int year, int month) { //TODO: to private
        var existingYear = workload.get(year);
        if (actionType == DELETE) {
            processDeleting(duration, month, existingYear);
        } else if (actionType == ADD) {
            processAdding(duration, workload, year, month, existingYear);
        }
    }

    /**
     * This method is used to process the addition of workload.
     * It checks if the workload for the given year already exists.
     * If it does, it checks if the workload for the given month exists.
     * If the month's workload exists, it adds the duration to the existing workload.
     * If the month's workload does not exist, it sets the workload for the month to the given duration.
     * If the workload for the year does not exist, it creates a new workload for the given month and year.
     *
     * @param duration     The duration of the training.
     * @param workload     The workload map to be processed.
     * @param year         The year of the training.
     * @param month        The month of the training.
     * @param existingYear The existing workload map for the given year.
     */
    private void processAdding(int duration, Map<Integer, Map<Integer, Integer>> workload, int year, int month,
                               Map<Integer, Integer> existingYear) {
        if (existingYear != null) {
            var currentDuration = existingYear.get(month);
            if (currentDuration != null) { //TODO: reason
                existingYear.put(month, currentDuration + duration);
            } else {
                existingYear.put(month, duration);
            }
        } else {
            addNewWorkloadForMonthAndYear(duration, month, workload, year);
        }
    }

    /**
     * This method is used to process the deletion of workload.
     * It checks if the workload for the given year already exists.
     * If it does, it checks if the workload for the given month exists.
     * If the month's workload exists, it subtracts the duration from the existing workload.
     * If the workload for the month becomes 0 after deletion, it removes the month from the workload.
     * If the month's workload does not exist, it throws a NoSuchElementException.
     * If the workload for the year does not exist, it throws a NoSuchElementException.
     *
     * @param duration     The duration of the training.
     * @param month        The month of the training.
     * @param existingYear The existing workload map for the given year.
     * @throws NoSuchElementException if the workload for the given year or month does not exist.
     */
    private void processDeleting(int duration, int month, Map<Integer, Integer> existingYear) {
        if (existingYear != null) {
            var currentDuration = existingYear.get(month);
            if (currentDuration != null) {
                existingYear.put(month, currentDuration - duration);
                if (existingYear.get(month) == 0) {
                    existingYear.remove(month);
                }
            } else {
                throw new NoSuchElementException("Month not found in workload");
            }
        } else {
            throw new NoSuchElementException("Year not found in workload");
        }
    }

    /**
     * This method is used to get the workload of an instructor. It checks if the workload for the given year already exists.
     * If it does, it processes the workload. If it does not, it creates a new workload.
     *
     * @param existingEntity The existing InstructorWorkloadEntity.
     * @param trainingDate   The date of the training.
     * @param duration       The duration of the training.
     * @param actionType     The type of action to be performed (ADD or DELETE).
     * @return The workload of the instructor.
     */
    private Map<Integer, Map<Integer, Integer>> updateWorkload(InstructorWorkloadEntity existingEntity,
                                                               LocalDate trainingDate, int duration,
                                                               ActionType actionType) {
        int year = trainingDate.getYear();
        int month = trainingDate.getMonthValue();
        Map<Integer, Map<Integer, Integer>> workload = existingEntity.getWorkload();
        if (workload.containsKey(year)) {
            processWorkload(duration, actionType, workload, year, month);
        } else {
            addNewWorkloadForMonthAndYear(duration, month, workload, year);
        }
        return workload;
    }

    /**
     * This method is used to create a new workload for a given month and year.
     *
     * @param duration The duration of the training.
     * @param month    The month of the training.
     * @param workload The workload map to which the new workload is to be added.
     * @param year     The year of the training.
     */
    private void addNewWorkloadForMonthAndYear(int duration, int month, Map<Integer, Map<Integer, Integer>> workload,
                                               int year) {
        Map<Integer, Integer> monthWorkload = Map.of(month, duration);
        workload.put(year, monthWorkload);
    }

    /**
     * This method is used to create a new workload for a given month and year.
     *
     * @param trainingDate The date of the training.
     * @param duration     The duration of the training.
     * @return The new workload map.
     */
    private Map<Integer, Map<Integer, Integer>> createNewWorkloadForMonthAndYear(LocalDate trainingDate, int duration) {
        Map<Integer, Map<Integer, Integer>> workload = new HashMap<>();
        Map<Integer, Integer> monthWorkload = new HashMap<>();
        monthWorkload.put(trainingDate.getMonthValue(), duration);
        workload.put(trainingDate.getYear(), monthWorkload);
        return workload;
    }
}
