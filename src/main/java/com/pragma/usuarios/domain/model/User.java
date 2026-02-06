package com.pragma.usuarios.domain.model;

import com.pragma.usuarios.domain.exception.UserUnderageException;
import java.time.LocalDate;
import java.time.Period;

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

    public User() {
    }

    public User(String firstName, String lastName, String identityDocument,
                String phone, LocalDate birthDate, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identityDocument = identityDocument;
        this.phone = phone;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String identityDocument,
                String phone, String email, String password, Long restaurantId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identityDocument = identityDocument;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.restaurantId = restaurantId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public void setIdentityDocument(String identityDocument) {
        this.identityDocument = identityDocument;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

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
