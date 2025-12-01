package com.pragma.usuarios.domain.usecase;

import com.pragma.usuarios.domain.api.IUserServicePort;
import com.pragma.usuarios.domain.exception.*;
import com.pragma.usuarios.domain.model.Role;
import com.pragma.usuarios.domain.model.User;
import com.pragma.usuarios.domain.spi.IPasswordEncoderPort;
import com.pragma.usuarios.domain.spi.IRolePersistencePort;
import com.pragma.usuarios.domain.spi.IUserPersistencePort;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Pattern;

public class UserUseCase implements IUserServicePort {

    private static final String ROLE_OWNER = "OWNER";
    private static final int MINIMUM_AGE = 18;
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

    public UserUseCase(IUserPersistencePort userPersistencePort,
                       IRolePersistencePort rolePersistencePort,
                       IPasswordEncoderPort passwordEncoderPort) {
        this.userPersistencePort = userPersistencePort;
        this.rolePersistencePort = rolePersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    public User createOwner(User user) {
        validateEmail(user.getEmail());
        validatePhone(user.getPhone());
        validateDocument(user.getIdentityDocument());
        validateAge(user.getBirthDate());
        validateUserDoesNotExist(user.getEmail(), user.getIdentityDocument());

        Role ownerRole = rolePersistencePort.findByName(ROLE_OWNER)
                .orElseThrow(() -> new RoleNotFoundException("Role OWNER does not exist in the system"));

        user.setRole(ownerRole);
        user.setPassword(passwordEncoderPort.encode(user.getPassword()));

        return userPersistencePort.saveUser(user);
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

    private void validateAge(LocalDate birthDate) {
        if (birthDate == null) {
            throw new UserUnderageException("Birth date is required");
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (age < MINIMUM_AGE) {
            throw new UserUnderageException("User must be of legal age (18 years or older)");
        }
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
