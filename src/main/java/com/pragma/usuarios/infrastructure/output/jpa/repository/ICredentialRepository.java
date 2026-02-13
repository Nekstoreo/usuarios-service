package com.pragma.usuarios.infrastructure.output.jpa.repository;

import com.pragma.usuarios.infrastructure.output.jpa.entity.CredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICredentialRepository extends JpaRepository<CredentialEntity, Long> {

    Optional<CredentialEntity> findByEmail(String email);

    Optional<CredentialEntity> findByUserId(Long userId);

    boolean existsByEmail(String email);
}