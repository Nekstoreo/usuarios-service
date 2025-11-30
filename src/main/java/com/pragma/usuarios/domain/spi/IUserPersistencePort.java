package com.pragma.usuarios.domain.spi;

import com.pragma.usuarios.domain.model.User;

import java.util.Optional;

public interface IUserPersistencePort {

    User saveUser(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdentityDocument(String identityDocument);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);
}
