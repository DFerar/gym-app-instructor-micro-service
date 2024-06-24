package com.gym.gymmicroservice.service;

import static com.gym.gymmicroservice.dto.request.ActionType.ADD;
import static com.gym.gymmicroservice.dto.request.ActionType.DELETE;

import com.gym.gymmicroservice.dto.request.ActionType;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.entity.MonthEntity;
import com.gym.gymmicroservice.entity.YearEntity;
import com.gym.gymmicroservice.repository.WorkloadRepository;
import java.time.LocalDate;
import java.util.*;

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
        if (existingEntity != null) {
            List<YearEntity> workload = updateWorkload(existingEntity, trainingDate, duration, actionType);
            entity.setWorkload(workload);
            log.info("Updating workload for instructor: {}", entity);
        } else {
            List<YearEntity> workload = createNewWorkloadForMonthAndYear(trainingDate, duration, actionType);
            entity.setWorkload(workload);
            log.info("Creating workload for instructor: {}", entity);
        }
        workloadRepository.save(entity);
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
    private List<YearEntity> updateWorkload(InstructorWorkloadEntity existingEntity,
                                                               LocalDate trainingDate, int duration,
                                                               ActionType actionType) {
        Integer year = trainingDate.getYear();
        Integer month = trainingDate.getMonthValue();
        List<YearEntity> workload = existingEntity.getWorkload();
        YearEntity yearEntity = workload.stream()
                .filter(y -> y.getYear().equals(year))
                .findFirst()
                .orElse(null);
        if (yearEntity != null) {
            processWorkloadForExistingYear(duration, actionType, yearEntity, month);
        } else {
            addWorkloadForYear(duration, month, year, workload);
        }
        return workload;
    }

    private void processWorkloadForExistingYear(int duration, ActionType actionType, YearEntity yearEntity, Integer month) {
        MonthEntity monthEntity = yearEntity.getMonths().stream()
                .filter(m -> m.getMonth().equals(month))
                .findFirst()
                .orElse(null);
        if (monthEntity != null) {
            processWorkload(duration, actionType, monthEntity);
        } else {
            addNewWorkloadForMonth(duration, month, yearEntity.getMonths());
        }
    }

    private void addWorkloadForYear(int duration, Integer month, Integer year, List<YearEntity> workload) {
        List<MonthEntity> monthWorkload = new ArrayList<>();
        MonthEntity newMonthEntity = new MonthEntity(month, duration);
        monthWorkload.add(newMonthEntity);
        YearEntity newYearEntity = new YearEntity(year, monthWorkload);
        workload.add(newYearEntity);
    }

    private void processWorkload(int duration, ActionType actionType, MonthEntity monthEntity) {
        if (actionType == ADD) {
            monthEntity.setWorkload(monthEntity.getWorkload() + duration);
        } else if (actionType == DELETE) {
            throwIfMonthEntityIsIllegal(duration, monthEntity);
            monthEntity.setWorkload(monthEntity.getWorkload() - duration);
        }
    }

    private void throwIfMonthEntityIsIllegal(int duration, MonthEntity monthEntity) {
        if (monthEntity == null) {
            throw new IllegalArgumentException("Month entity cannot be null");
        }
        if (monthEntity.getWorkload() < duration) {
            throw new IllegalArgumentException("Workload cannot be negative");
        }
    }

    private void addNewWorkloadForMonth(int duration, int month, List<MonthEntity> months) {
        MonthEntity newMonthEntity = new MonthEntity(month, duration);
        months.add(newMonthEntity);
    }

    /**
     * This method is used to create a new workload for a given month and year.
     *
     * @param trainingDate The date of the training.
     * @param duration     The duration of the training.
     * @return The new workload map.
     */
    private List<YearEntity> createNewWorkloadForMonthAndYear(LocalDate trainingDate, int duration, ActionType actionType) {
        if (actionType == DELETE) {
            throw new IllegalArgumentException("You cannot delete a workload for a non-existing instructor workload");
        }
        List<YearEntity> workload = new ArrayList<>();
        List<MonthEntity> monthWorkload = new ArrayList<>();
        MonthEntity monthEntity = new MonthEntity(trainingDate.getMonthValue(), duration);
        monthWorkload.add(monthEntity);
        YearEntity yearEntity = new YearEntity(trainingDate.getYear(), monthWorkload);
        workload.add(yearEntity);
        return workload;
    }
}
