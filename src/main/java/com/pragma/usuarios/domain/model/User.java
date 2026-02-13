package com.pragma.usuarios.domain.model;

import com.pragma.usuarios.domain.exception.UserUnderageException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private static final int MINIMUM_AGE = 18;

    private Long id;
    private String firstName;
    private String lastName;
    private String identityDocument;
    private String phone;
    private LocalDate birthDate;
    private String email;
    private String password;
    private Role role;
    private Long restaurantId;

    public void validateAge() {
        if (this.birthDate == null) {
            throw new UserUnderageException("Birth date is required");
        }

        int age = Period.between(this.birthDate, LocalDate.now()).getYears();

        if (age < MINIMUM_AGE) {
            throw new UserUnderageException("User must be of legal age (18 years or older)");
        }
    }
}
