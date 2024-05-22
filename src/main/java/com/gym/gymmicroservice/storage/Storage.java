package com.gym.gymmicroservice.storage;

import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class Storage {
    private final Map<String, InstructorWorkloadEntity> storage = new ConcurrentHashMap<>();

    /**
     * This method is used to save an InstructorWorkloadEntity to the storage.
     * It uses the username of the entity as the key and the entity itself as the value.
     * After saving the entity, it logs the current state of the storage.
     *
     * @param entity The InstructorWorkloadEntity to be saved.
     */
    public void save(InstructorWorkloadEntity entity) {
        storage.put(entity.getUsername(), entity);
        log.info("Storage: {}", storage);
    }

    /**
     * This method is used to retrieve an InstructorWorkloadEntity from the storage.
     * It uses the provided username as the key to retrieve the entity.
     *
     * @param username The username of the InstructorWorkloadEntity to be retrieved.
     * @return InstructorWorkloadEntity Returns the InstructorWorkloadEntity associated with the provided username.
     */
    public InstructorWorkloadEntity get(String username) {
        return storage.get(username);
    }
}
