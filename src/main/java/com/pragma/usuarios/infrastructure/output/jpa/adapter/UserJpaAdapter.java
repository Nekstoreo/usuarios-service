package com.pragma.usuarios.infrastructure.output.jpa.adapter;

import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IUserPersistencePort;
import com.pragma.usuarios.infrastructure.output.jpa.entity.CredentialEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.EmployeeRestaurantEntity;
import com.pragma.usuarios.infrastructure.output.jpa.entity.UserEntity;
import com.pragma.usuarios.infrastructure.output.jpa.mapper.UserEntityMapper;
import com.pragma.usuarios.infrastructure.output.jpa.repository.ICredentialRepository;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IEmployeeRestaurantRepository;
import com.pragma.usuarios.infrastructure.output.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository userRepository;
    private final ICredentialRepository credentialRepository;
    private final IEmployeeRestaurantRepository employeeRestaurantRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    @Transactional
    public User saveUser(User user) {
        UserEntity userEntity = userEntityMapper.toEntity(user);
        UserEntity savedEntity = userRepository.save(userEntity);

        CredentialEntity credentialEntity = CredentialEntity.builder()
                .user(savedEntity)
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
        CredentialEntity savedCredential = credentialRepository.save(credentialEntity);

        Optional<EmployeeRestaurantEntity> employeeRestaurant = Optional.empty();
        if (user.getRestaurantId() != null) {
            EmployeeRestaurantEntity employeeRestaurantEntity = EmployeeRestaurantEntity.builder()
                    .user(savedEntity)
                    .restaurantId(user.getRestaurantId())
                    .build();
            employeeRestaurant = Optional.of(employeeRestaurantRepository.save(employeeRestaurantEntity));
        }

        return toModel(savedEntity, savedCredential, employeeRestaurant);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id)
                .flatMap(this::toModel);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return credentialRepository.findByEmail(email)
                .map(credential -> toModel(credential.getUser(), credential, employeeRestaurantRepository.findByUserId(credential.getUser().getId())));
    }

    @Override
    public Optional<User> findByIdentityDocument(String identityDocument) {
        return userRepository.findByIdentityDocument(identityDocument)
                .flatMap(this::toModel);
    }

    @Override
    public boolean existsByEmail(String email) {
        return credentialRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByIdentityDocument(String identityDocument) {
        return userRepository.existsByIdentityDocument(identityDocument);
    }

    private Optional<User> toModel(UserEntity userEntity) {
        return credentialRepository.findByUserId(userEntity.getId())
                .map(credential -> toModel(userEntity, credential, employeeRestaurantRepository.findByUserId(userEntity.getId())));
    }

    private User toModel(UserEntity userEntity,
                         CredentialEntity credentialEntity,
                         Optional<EmployeeRestaurantEntity> employeeRestaurant) {
        User user = userEntityMapper.toModel(userEntity);
        user.setEmail(credentialEntity.getEmail());
        user.setPassword(credentialEntity.getPassword());
        user.setRestaurantId(employeeRestaurant.map(EmployeeRestaurantEntity::getRestaurantId).orElse(null));
        return user;
    }
}
