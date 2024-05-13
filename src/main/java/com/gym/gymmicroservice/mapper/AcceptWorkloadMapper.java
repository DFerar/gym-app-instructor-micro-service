package com.gym.gymmicroservice.mapper;

import com.gym.gymmicroservice.dto.request.AcceptWorkloadRequestDto;
import com.gym.gymmicroservice.entity.InstructorStatus;
import com.gym.gymmicroservice.entity.InstructorWorkloadEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AcceptWorkloadMapper {
    AcceptWorkloadMapper INSTANCE = Mappers.getMapper(AcceptWorkloadMapper.class);

    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "isActive", target = "status", qualifiedByName = "statusMapping")
    InstructorWorkloadEntity dtoToEntity(AcceptWorkloadRequestDto dto);

    @Named("statusMapping")
    default InstructorStatus statusMapping(Boolean isActive) {
        return isActive ? InstructorStatus.ACTIVE : InstructorStatus.INACTIVE;
    }
}
