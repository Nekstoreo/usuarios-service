package com.pragma.usuarios.domain.api;

import com.pragma.usuarios.domain.model.User;

import java.util.Optional;

public interface IUserServicePort {

    User createOwner(User user);

    User createEmployee(User user);

    User createClient(User user);

    Optional<User> getUserById(Long id);
}
