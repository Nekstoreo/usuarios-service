package com.pragma.usuarios.infrastructure.initialization;

import com.pragma.usuarios.infrastructure.configuration.AdminProperties;
import com.pragma.usuarios.infrastructure.output.jpa.entity.RoleEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.UserEntity;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IRoleRepository;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataInitializer is responsible for initializing system data when the application starts.
 * It creates the default admin user if it doesn't exist.
 *
 * This approach is better than inserting directly in data.sql because:
 * - It ensures the password is encoded using the same PasswordEncoder configured in the application
 * - It can validate business rules before insertion
 * - It's more maintainable and testable
 * - It prevents hardcoding passwords in plain files
 */
@Slf4j
@Component
public class DataInitializer {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    public DataInitializer(IUserRepository userRepository,
                          IRoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          AdminProperties adminProperties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeData() {
        log.info("Initializing system data...");
        initializeAdminUser();
        log.info("System data initialization completed");
    }

    private void initializeAdminUser() {
        String adminEmail = adminProperties.getEmail();

        // Check if admin already exists
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.info("Admin user already exists, skipping creation");
            return;
        }

        // Get or create ADMIN role
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found. Make sure data.sql has been executed."));

        // Create admin user
        UserEntity adminUser = new UserEntity();
        adminUser.setFirstName(adminProperties.getFirstName());
        adminUser.setLastName(adminProperties.getLastName());
        adminUser.setIdentityDocument(adminProperties.getIdentityDocument());
        adminUser.setPhone(adminProperties.getPhone());
        adminUser.setBirthDate(adminProperties.getBirthDate());
        adminUser.setEmail(adminEmail);
        // Encode password using the application's PasswordEncoder
        adminUser.setPassword(passwordEncoder.encode(adminProperties.getPassword()));
        adminUser.setRole(adminRole);

        userRepository.save(adminUser);
        log.info("Default admin user created successfully");
    }
}
