package com.pragma.usuarios.infrastructure.output.jpa.repository;

import com.pragma.usuarios.infrastructure.output.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByIdentityDocument(String identityDocument);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);
}
