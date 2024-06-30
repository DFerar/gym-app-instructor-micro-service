package com.gym.gymmicroservice.entity;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class InstructorWorkloadEntity {
    @Id
    private String username;
    @Indexed
    private String firstName;
    @Indexed
    private String lastName;
    private InstructorStatus status;
    private List<YearEntity> workload;
}
