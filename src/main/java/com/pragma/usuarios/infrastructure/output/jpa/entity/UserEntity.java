package com.pragma.usuarios.infrastructure.output.jpa.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "identity_document", nullable = false, unique = true, length = 20)
    private String identityDocument;

    @Column(name = "phone", nullable = false, length = 13)
    private String phone;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    public UserEntity() {
    }

    public UserEntity(String firstName, String lastName, String identityDocument,
                      String phone, LocalDate birthDate, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identityDocument = identityDocument;
        this.phone = phone;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
    }

    public static UserEntityBuilder builder() {
        return new UserEntityBuilder();
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

    public RoleEntity getRole() {
        return role;
    }

    public void setRole(RoleEntity role) {
        this.role = role;
    }

    public static class UserEntityBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private String identityDocument;
        private String phone;
        private LocalDate birthDate;
        private String email;
        private String password;
        private RoleEntity role;

        public UserEntityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserEntityBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserEntityBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserEntityBuilder identityDocument(String identityDocument) {
            this.identityDocument = identityDocument;
            return this;
        }

        public UserEntityBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserEntityBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public UserEntityBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserEntityBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserEntityBuilder role(RoleEntity role) {
            this.role = role;
            return this;
        }

        public UserEntity build() {
            UserEntity userEntity = new UserEntity(firstName, lastName, identityDocument, phone, birthDate, email, password);
            userEntity.setId(id);
            userEntity.setRole(role);
            return userEntity;
        }
    }
}


