package com.pragma.usuarios.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    private static final Long ADMIN_ROLE_ID = 1L;
    private static final String ADMIN_ROLE_NAME = "ROLE_ADMIN";
    private static final String ADMIN_ROLE_DESCRIPTION = "Administrator role";
    private static final Long USER_ROLE_ID = 2L;
    private static final String USER_ROLE_NAME = "ROLE_USER";
    private static final String USER_ROLE_DESCRIPTION = "User role";

    @Test
    void role_ShouldStoreDataCorrectly() {
        Role role = new Role(ADMIN_ROLE_ID, ADMIN_ROLE_NAME, ADMIN_ROLE_DESCRIPTION);

        assertEquals(ADMIN_ROLE_ID, role.getId());
        assertEquals(ADMIN_ROLE_NAME, role.getName());
        assertEquals(ADMIN_ROLE_DESCRIPTION, role.getDescription());
    }

    @Test
    void setters_ShouldUpdateFields() {
        Role role = new Role();
        role.setId(USER_ROLE_ID);
        role.setName(USER_ROLE_NAME);
        role.setDescription(USER_ROLE_DESCRIPTION);

        assertEquals(USER_ROLE_ID, role.getId());
        assertEquals(USER_ROLE_NAME, role.getName());
        assertEquals(USER_ROLE_DESCRIPTION, role.getDescription());
    }
}
