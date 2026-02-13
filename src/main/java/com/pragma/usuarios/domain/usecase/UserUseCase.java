package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.api.IUserServicePort;
import com.pragma.usuarios.domain.exception.*;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IPasswordEncoderPort;
import com.pragma.usuarios.domain.spi.IRolePersistencePort;
import com.pragma.usuarios.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserUseCase implements IUserServicePort {

    private static final String ROLE_OWNER = "OWNER";
    private static final String ROLE_EMPLOYEE = "EMPLOYEE";
    private static final String ROLE_CLIENT = "CLIENT";
    private static final int MAX_PHONE_LENGTH = 13;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z\\d+_.-]+@[A-Za-z\\d.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?\\d{1,12}$"
    );

    private static final Pattern DOCUMENT_PATTERN = Pattern.compile(
            "^\\d+$"
    );

    private final IUserPersistencePort userPersistencePort;
    private final IRolePersistencePort rolePersistencePort;
    private final IPasswordEncoderPort passwordEncoderPort;

    @Override
    public User createOwner(User user) {
        validateCommon(user);
        validateAge(user);
        return saveWithRole(user, ROLE_OWNER);
    }

    @Override
    public User createEmployee(User user) {
        validateCommon(user);
        validateRestaurantId(user.getRestaurantId());
        return saveWithRole(user, ROLE_EMPLOYEE);
    }

    @Override
    public User createClient(User user) {
        validateCommon(user);
        return saveWithRole(user, ROLE_CLIENT);
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
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.length() > MAX_PHONE_LENGTH || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidPhoneException(
                    "Phone must have a maximum of 13 characters and may contain the + symbol. Example: +573005698325"
            );
        }
    }

    private void validateDocument(String document) {
        if (document == null || !DOCUMENT_PATTERN.matcher(document).matches()) {
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
