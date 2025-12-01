package com.pragma.usuarios.application.mapper;

import com.pragma.usuarios.application.dto.request.CreateClientRequest;
import com.pragma.usuarios.application.dto.request.CreateEmployeeRequest;
import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    User toUser(CreateOwnerRequest createOwnerRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(CreateEmployeeRequest createEmployeeRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    User toUser(CreateClientRequest createClientRequest);
}
