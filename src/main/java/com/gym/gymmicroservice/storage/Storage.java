package com.gym.gymmicroservice.storage;

import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Storage {
    private final Map<String, InstructorWorkloadEntity> storage = new ConcurrentHashMap<>();

    public void save(InstructorWorkloadEntity entity) {
        storage.put(entity.getUsername(), entity);
    }

    public InstructorWorkloadEntity get(String username) {
        return storage.get(username);
    }

    public void delete(String username) {
        storage.remove(username);
    }
}
