package com.pragma.usuarios.infrastructure.output.jpa.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class UserEntityTest {
    private static final String FIRST_NAME = "Jane";
    private static final String LAST_NAME = "Doe";
    private static final String IDENTITY_DOCUMENT = "ID123";
    private static final String PHONE = "+12345";
    private static final Long ROLE_ID = 1L;
    private static final String ROLE_NAME = "ROLE_ADMIN";
    private static final String ROLE_DESCRIPTION = "Admin";
    private static final Long USER_ID = 1L;
    private static final Long ANOTHER_USER_ID = 10L;
    private static final String ANOTHER_NAME = "Name";

    @Test
    void userEntity_ShouldStoreDataCorrectly_ViaConstructor() {
        LocalDate birthDate = LocalDate.of(1995, 5, 5);
        UserEntity entity = UserEntity.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .identityDocument(IDENTITY_DOCUMENT)
                .phone(PHONE)
                .birthDate(birthDate)
                .build();

        assertEquals(FIRST_NAME, entity.getFirstName());
        assertEquals(LAST_NAME, entity.getLastName());
        assertEquals(IDENTITY_DOCUMENT, entity.getIdentityDocument());
        assertEquals(PHONE, entity.getPhone());
        assertEquals(birthDate, entity.getBirthDate());
    }

    @Test
    void userEntity_ShouldStoreDataCorrectly_ViaBuilder() {
        RoleEntity role = RoleEntity.builder().id(ROLE_ID).name(ROLE_NAME).description(ROLE_DESCRIPTION).build();
        LocalDate birthDate = LocalDate.of(1995, 5, 5);

        UserEntity entity = UserEntity.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .identityDocument(IDENTITY_DOCUMENT)
                .phone(PHONE)
                .birthDate(birthDate)
                .role(role)
                .build();

        assertEquals(USER_ID, entity.getId());
        assertEquals(FIRST_NAME, entity.getFirstName());
        assertEquals(LAST_NAME, entity.getLastName());
        assertEquals(IDENTITY_DOCUMENT, entity.getIdentityDocument());
        assertEquals(PHONE, entity.getPhone());
        assertEquals(birthDate, entity.getBirthDate());
        assertEquals(role, entity.getRole());
    }

    @Test
    void setters_ShouldUpdateFields() {
        UserEntity entity = new UserEntity();
        entity.setId(ANOTHER_USER_ID);
        entity.setFirstName(ANOTHER_NAME);

        assertEquals(ANOTHER_USER_ID, entity.getId());
        assertEquals(ANOTHER_NAME, entity.getFirstName());
    }
}
