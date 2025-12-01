package com.pragma.usuarios.application.handler;

import com.pragma.usuarios.application.dto.request.LoginRequest;
import com.pragma.usuarios.application.dto.response.AuthResponse;

public interface IAuthHandler {

    AuthResponse login(LoginRequest request);
}
