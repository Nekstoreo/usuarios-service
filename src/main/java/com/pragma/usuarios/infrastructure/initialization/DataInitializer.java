package com.pragma.usuarios.infrastructure.initialization;

import com.pragma.usuarios.domain.exception.RoleInitializationException;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IUserPersistencePort;
import com.pragma.usuarios.infrastructure.configuration.AdminProperties;
import com.pragma.usuarios.infrastructure.output.jpa.entity.RoleEntity;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final IUserPersistencePort userPersistencePort;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("Initializing system data...");
        initializeAdminUser();
        log.info("System data initialization completed");
    }

    private void initializeAdminUser() {
        String adminEmail = adminProperties.getEmail();

        if (userPersistencePort.existsByEmail(adminEmail)) {
            log.info("Admin user already exists, skipping creation");
            return;
        }

        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RoleInitializationException("ADMIN role not found. Make sure data.sql has been executed."));

        Role role = new Role(adminRole.getId(), adminRole.getName(), adminRole.getDescription());

        User adminUser = User.builder()
            .firstName(adminProperties.getFirstName())
            .lastName(adminProperties.getLastName())
            .identityDocument(adminProperties.getIdentityDocument())
            .phone(adminProperties.getPhone())
            .birthDate(adminProperties.getBirthDate())
            .email(adminEmail)
            .password(passwordEncoder.encode(adminProperties.getPassword()))
            .role(role)
            .build();

        userPersistencePort.saveUser(adminUser);
        log.info("Default admin user created successfully");
    }
}
