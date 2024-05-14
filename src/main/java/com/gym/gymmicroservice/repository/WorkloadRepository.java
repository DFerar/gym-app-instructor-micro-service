package com.gym.gymmicroservice.repository;

import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import com.gym.gymmicroservice.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkloadRepository {
    private final Storage storage;
    /**
     * This method is used to find an InstructorWorkloadEntity by its username.
     * It retrieves the entity from the storage using the provided username as the key.
     *
     * @param username The username of the InstructorWorkloadEntity to be retrieved.
     * @return InstructorWorkloadEntity Returns the InstructorWorkloadEntity associated with the provided username.
     * If no entity is found, it returns null.
     */
    public InstructorWorkloadEntity findByUsername(String username) {
        return storage.get(username);
    }

    /**
     * This method is used to save an InstructorWorkloadEntity to the storage.
     * It uses the save method of the storage to persist the entity.
     *
     * @param existingEntity The InstructorWorkloadEntity to be saved.
     */
    public void save(InstructorWorkloadEntity existingEntity) {
        storage.save(existingEntity);
    }
}
