package com.pragma.usuarios.application.mapper;

import com.pragma.usuarios.application.dto.response.UserResponse;
import com.pragma.usuarios.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserResponseMapper {

    @Mapping(source = "role.name", target = "role")
    UserResponse toResponse(User user);
}
