package com.gym.gymmicroservice.mapper;

import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.entity.InstructorStatus;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AcceptWorkloadMapper {

    @Mapping(source = "isActive", target = "status", qualifiedByName = "statusMapping")
    InstructorWorkloadEntity dtoToEntity(AcceptWorkloadRequestDto dto);

    @Named("statusMapping")
    default InstructorStatus statusMapping(Boolean isActive) {
        return isActive ? InstructorStatus.ACTIVE : InstructorStatus.INACTIVE;
    }
}
