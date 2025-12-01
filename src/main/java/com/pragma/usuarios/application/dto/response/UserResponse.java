package com.pragma.usuarios.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String identityDocument;
    private String phone;
    private LocalDate birthDate;
    private String email;
    private String role;
    private Long restaurantId;

}
