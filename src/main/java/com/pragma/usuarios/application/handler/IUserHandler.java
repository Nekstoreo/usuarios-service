package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.CreateEmployeeRequest;
import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.application.dto.response.UserResponse;

import java.util.Optional;

public interface IUserHandler {

    UserResponse createOwner(CreateOwnerRequest createOwnerRequest);

    UserResponse createEmployee(CreateEmployeeRequest createEmployeeRequest);

    Optional<UserResponse> getUserById(Long id);
}
