package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.api.IUserServicePort;
import com.pragma.usuarios.domain.exception.*;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IPasswordEncoderPort;
import com.pragma.usuarios.domain.spi.IRolePersistencePort;
import com.pragma.usuarios.domain.spi.IUserPersistencePort;
import com.pragma.usuarios.infrastructure.constant.SecurityConstants;
import com.pragma.usuarios.infrastructure.constant.ValidationConstants;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserUseCase implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    @Override
    public User createOwner(User user) {
        validateCommon(user);
        validateAge(user);
        return saveWithRole(user, SecurityConstants.ROLE_OWNER);
    }

    @Override
    public User createEmployee(User user) {
        validateCommon(user);
        validateRestaurantId(user.getRestaurantId());
        return saveWithRole(user, SecurityConstants.ROLE_EMPLOYEE);
    }

    @Override
    public User createClient(User user) {
        validateCommon(user);
        return saveWithRole(user, SecurityConstants.ROLE_CLIENT);
    }

    private void validateCommon(User user) {
        validateEmail(user.getEmail());
        validatePhone(user.getPhone());
        validateDocument(user.getIdentityDocument());
        validateUserDoesNotExist(user.getEmail(), user.getIdentityDocument());
    }

    private User saveWithRole(User user, String roleName) {
        Role role = rolePersistencePort.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role " + roleName + " does not exist in the system"));

        user.setRole(role);
        user.setPassword(passwordEncoderPort.encode(user.getPassword()));

        return userPersistencePort.saveUser(user);
    }

    private void validateRestaurantId(Long restaurantId) {
        if (restaurantId == null) {
            throw new InvalidRestaurantException("Restaurant ID is required for employee creation");
        }
    }

    private void validateEmail(String email) {
        if (email == null || !ValidationConstants.EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.length() > ValidationConstants.MAX_PHONE_LENGTH || !ValidationConstants.PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidPhoneException(
                    "Phone must have a maximum of 13 characters and may contain the + symbol. Example: +573005698325"
            );
        }
    }

    private void validateDocument(String document) {
        if (document == null || !ValidationConstants.DOCUMENT_PATTERN.matcher(document).matches()) {
            throw new InvalidDocumentException("Identity document must be numeric only");
        }
    }

    private void validateAge(User user) {
        user.validateAge();
    }

    private void validateUserDoesNotExist(String email, String document) {
        if (userPersistencePort.existsByEmail(email)) {
            throw new UserAlreadyExistsException("A user already exists with email: " + email);
        }

        if (userPersistencePort.existsByIdentityDocument(document)) {
            throw new UserAlreadyExistsException("A user already exists with document: " + document);
        }
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userPersistencePort.findById(id);
    }
}
