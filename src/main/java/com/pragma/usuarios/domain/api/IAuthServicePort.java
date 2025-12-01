package com.pragma.usuarios.domain.api;

import com.pragma.usuarios.domain.model.User;

public interface IAuthServicePort {

    String authenticate(String email, String password);

    User validateToken(String token);
}
