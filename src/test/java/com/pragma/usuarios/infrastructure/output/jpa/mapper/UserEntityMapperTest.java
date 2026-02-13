package com.pragma.usuarios.infrastructure.output.jpa.mapper;

import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.infrastructure.output.jpa.entity.RoleEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("UserEntityMapper Tests")
class UserEntityMapperTest {

    private UserEntityMapper userEntityMapper;

    private static final Long USER_ID = 1L;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String IDENTITY_DOCUMENT = "123456789";
    private static final String PHONE = "+573001234567";
    private static final LocalDate BIRTH_DATE = LocalDate.of(1990, 1, 1);

    private static final Long ROLE_ID = 1L;
    private static final String ROLE_NAME = "OWNER";
    private static final String ROLE_DESCRIPTION = "Restaurant Owner";

    @BeforeEach
    void setUp() {
        userEntityMapper = Mappers.getMapper(UserEntityMapper.class);
        RoleEntityMapper roleEntityMapper = Mappers.getMapper(RoleEntityMapper.class);
        ReflectionTestUtils.setField(userEntityMapper, "roleEntityMapper", roleEntityMapper);
    }

    @Test
    @DisplayName("Should map User model to UserEntity")
    void shouldMapModelToEntity() {
        Role role = new Role(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);
        User user = new User();
        user.setId(USER_ID);
        user.setFirstName(FIRST_NAME);
        user.setLastName(LAST_NAME);
        user.setIdentityDocument(IDENTITY_DOCUMENT);
        user.setPhone(PHONE);
        user.setBirthDate(BIRTH_DATE);
        user.setRole(role);

        UserEntity userEntity = userEntityMapper.toEntity(user);

        assertNotNull(userEntity);
        assertEquals(USER_ID, userEntity.getId());
        assertEquals(FIRST_NAME, userEntity.getFirstName());
        assertEquals(LAST_NAME, userEntity.getLastName());
        assertEquals(IDENTITY_DOCUMENT, userEntity.getIdentityDocument());
        assertEquals(PHONE, userEntity.getPhone());
        assertEquals(BIRTH_DATE, userEntity.getBirthDate());
        assertNull(userEntity.getCreatedAt());
        assertNull(userEntity.getUpdatedAt());
        
        assertNotNull(userEntity.getRole());
        assertEquals(ROLE_ID, userEntity.getRole().getId());
        assertEquals(ROLE_NAME, userEntity.getRole().getName());
        assertEquals(ROLE_DESCRIPTION, userEntity.getRole().getDescription());
    }

    @Test
    @DisplayName("Should map UserEntity to User model")
    void shouldMapEntityToModel() {
        RoleEntity roleEntity = new RoleEntity(ROLE_ID, ROLE_NAME, ROLE_DESCRIPTION);
        UserEntity userEntity = UserEntity.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .identityDocument(IDENTITY_DOCUMENT)
                .phone(PHONE)
                .birthDate(BIRTH_DATE)
                .role(roleEntity)
                .build();

        User user = userEntityMapper.toModel(userEntity);

        assertNotNull(user);
        assertEquals(USER_ID, user.getId());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(IDENTITY_DOCUMENT, user.getIdentityDocument());
        assertEquals(PHONE, user.getPhone());
        assertEquals(BIRTH_DATE, user.getBirthDate());
        assertNull(user.getEmail());
        assertNull(user.getPassword());
        assertNull(user.getRestaurantId());

        assertNotNull(user.getRole());
        assertEquals(ROLE_ID, user.getRole().getId());
        assertEquals(ROLE_NAME, user.getRole().getName());
        assertEquals(ROLE_DESCRIPTION, user.getRole().getDescription());
    }

    @Test
    @DisplayName("Should return null when source is null")
    void shouldReturnNullWhenSourceIsNull() {
        assertNull(userEntityMapper.toEntity(null));
        assertNull(userEntityMapper.toModel(null));
    }
}
