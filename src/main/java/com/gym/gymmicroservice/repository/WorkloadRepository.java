package com.gym.gymmicroservice.repository;

import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkloadRepository {
    private final Storage storage;

    public InstructorWorkloadEntity findByUsername(String username) {
        return storage.get(username);
    }

    public void save(InstructorWorkloadEntity existingEntity) {
        storage.save(existingEntity);
    }
}
