package com.gym.gymmicroservice.entity;

import java.time.Month;
import java.time.Year;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorWorkloadEntity {
    private String username;
    private String firstName;
    private String lastName;
    private InstructorStatus status;
    private Map<Integer, Map<Integer, Integer>> workload;
}
