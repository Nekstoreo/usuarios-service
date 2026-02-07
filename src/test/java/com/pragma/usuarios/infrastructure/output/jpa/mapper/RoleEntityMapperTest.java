package com.pragma.usuarios.infrastructure.output.jpa.mapper;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.infrastructure.output.jpa.entity.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("RoleEntityMapper Tests")
class RoleEntityMapperTest {

    private static final Long ROLE_ID = 1L;
    private static final String ROLE_NAME = "ADMIN";
    private static final String ROLE_DESCRIPTION = "Administrator role";

    private RoleEntityMapper roleEntityMapper;

    @BeforeEach
    void setUp() {
        roleEntityMapper = Mappers.getMapper(RoleEntityMapper.class);
    }

    @Test
    @DisplayName("Should map Role model to RoleEntity")
    void shouldMapModelToEntity() {
        Role role = new Role(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);

        RoleEntity roleEntity = roleEntityMapper.toEntity(role);

        assertNotNull(roleEntity);
        assertEquals(ROLE_ID, roleEntity.getId());
        assertEquals(ROLE_NAME, roleEntity.getName());
        assertEquals(ROLE_DESCRIPTION, roleEntity.getDescription());
    }

    @Test
    @DisplayName("Should map RoleEntity to Role model")
    void shouldMapEntityToModel() {
        RoleEntity roleEntity = new RoleEntity(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);

        Role role = roleEntityMapper.toModel(roleEntity);

        assertNotNull(role);
        assertEquals(ROLE_ID, role.getId());
        assertEquals(ROLE_NAME, role.getName());
        assertEquals(ROLE_DESCRIPTION, role.getDescription());
    }

    @Test
    @DisplayName("Should return null when source is null")
    void shouldReturnNullWhenSourceIsNull() {
        assertNull(roleEntityMapper.toEntity(null));
        assertNull(roleEntityMapper.toModel(null));
    }
}
