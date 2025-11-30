package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.CreateOwnerRequest;
import com.pragma.usuarios.application.dto.response.UserResponse;

public interface IUserHandler {

    UserResponse createOwner(CreateOwnerRequest createOwnerRequest);
}
