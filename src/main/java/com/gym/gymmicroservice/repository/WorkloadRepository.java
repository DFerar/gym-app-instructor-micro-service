package com.gym.gymmicroservice.repository;

import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkloadRepository extends MongoRepository<InstructorWorkloadEntity, Long>{
    InstructorWorkloadEntity findByUsername(String username);
}
