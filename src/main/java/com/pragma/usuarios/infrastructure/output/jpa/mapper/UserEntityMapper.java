package com.pragma.usuarios.infrastructure.output.jpa.mapper;

import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.infrastructure.output.jpa.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {RoleEntityMapper.class})
public interface UserEntityMapper {

    UserEntity toEntity(User user);

    User toModel(UserEntity userEntity);
}
