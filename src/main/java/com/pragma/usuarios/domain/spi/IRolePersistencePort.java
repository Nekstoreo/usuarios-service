package com.pragma.usuarios.domain.spi;

import com.pragma.usuarios.domain.model.Role;

import java.util.Optional;

public interface IRolePersistencePort {

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);
}
