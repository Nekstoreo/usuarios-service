package com.pragma.usuarios.infrastructure.output.jpa.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleEntityTest {
    private static final Long ADMIN_ROLE_ID = 1L;
    private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    private static final String ADMIN_ROLE_DESCRIPTION = "Administrator";
    private static final Long USER_ROLE_ID = 2L;
    private static final String USER_ROLE_NAME = "ROLE_USER";
    private static final String USER_ROLE_DESCRIPTION = "User info";

    @Test
    void roleEntity_ShouldStoreDataCorrectly() {
        RoleEntity entity = new RoleEntity(ADMIN_ROLE_ID, ADMIN_ROLE_NAME, ADMIN_ROLE_DESCRIPTION);

        assertEquals(ADMIN_ROLE_ID, entity.getId());
        assertEquals(ADMIN_ROLE_NAME, entity.getName());
        assertEquals(ADMIN_ROLE_DESCRIPTION, entity.getDescription());
    }

    @Test
    void setters_ShouldUpdateFields() {
        RoleEntity entity = new RoleEntity();
        entity.setId(USER_ROLE_ID);
        entity.setName(USER_ROLE_NAME);
        entity.setDescription(USER_ROLE_DESCRIPTION);

        assertEquals(USER_ROLE_ID, entity.getId());
        assertEquals(USER_ROLE_NAME, entity.getName());
        assertEquals(USER_ROLE_DESCRIPTION, entity.getDescription());
    }
}
