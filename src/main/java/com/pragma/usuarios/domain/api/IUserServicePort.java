package com.pragma.usuarios.domain.api;

import com.pragma.usuarios.domain.model.User;

public interface IUserServicePort {

    User createOwner(User user);
}
