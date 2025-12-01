package com.pragma.usuarios.infrastructure.output.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
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

    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @Column(name = "restaurant_id")
    private Long restaurantId;

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

    public UserEntity(UserEntityBuilder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.identityDocument = builder.identityDocument;
        this.phone = builder.phone;
        this.birthDate = builder.birthDate;
        this.email = builder.email;
        this.password = builder.password;
        this.role = builder.role;
        this.restaurantId = builder.restaurantId;
    }

    public static UserEntityBuilder builder() {
        return new UserEntityBuilder();
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
        private Long restaurantId;

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

        public UserEntityBuilder restaurantId(Long restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public UserEntity build() {
            UserEntity userEntity = new UserEntity(this);
            userEntity.setId(id);
            return userEntity;
        }
    }
}
