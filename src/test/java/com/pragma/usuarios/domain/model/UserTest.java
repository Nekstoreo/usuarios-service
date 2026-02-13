package com.pragma.usuarios.domain.model;

import com.pragma.usuarios.domain.exception.UserUnderageException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private static final Long ROLE_ID = 1L;
    private static final String ROLE_NAME = "ROLE_ADMIN";
    private static final String ROLE_DESCRIPTION = "Admin";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String IDENTITY_DOCUMENT = "123456";
    private static final String PHONE = "+123456789";
    private static final String EMAIL = "john@doe.com";
    private static final String PASSWORD = "password";
    private static final Long RESTAURANT_ID = 10L;

    @Test
    void user_ShouldStoreDataCorrectly() {
        Role role = Role.builder().id(ROLE_ID).name(ROLE_NAME).description(ROLE_DESCRIPTION).build();
        LocalDate birthDate = LocalDate.of(1990, 1, 1);

        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .identityDocument(IDENTITY_DOCUMENT)
                .phone(PHONE)
                .birthDate(birthDate)
                .email(EMAIL)
                .password(PASSWORD)
                .id(ROLE_ID)
                .role(role)
                .restaurantId(RESTAURANT_ID)
                .build();

        assertEquals(ROLE_ID, user.getId());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(IDENTITY_DOCUMENT, user.getIdentityDocument());
        assertEquals(PHONE, user.getPhone());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals(EMAIL, user.getEmail());
        assertEquals(PASSWORD, user.getPassword());
        assertEquals(role, user.getRole());
        assertEquals(RESTAURANT_ID, user.getRestaurantId());
    }

    @Test
    void validateAge_ShouldNotThrowException_WhenUserIsAdult() {
        LocalDate adultDate = LocalDate.now().minusYears(18);
        User user = new User();
        user.setBirthDate(adultDate);

        assertDoesNotThrow(user::validateAge);
    }

    @Test
    void validateAge_ShouldThrowException_WhenUserIsUnderage() {
        LocalDate youngDate = LocalDate.now().minusYears(17);
        User user = new User();
        user.setBirthDate(youngDate);

        assertThrows(UserUnderageException.class, user::validateAge);
    }

    @Test
    void validateAge_ShouldThrowException_WhenBirthDateIsNull() {
        User user = new User();
        user.setBirthDate(null);

        assertThrows(UserUnderageException.class, user::validateAge);
    }
}
