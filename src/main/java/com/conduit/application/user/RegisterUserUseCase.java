package com.conduit.application.user;

import com.conduit.domain.user.User;

public class RegisterUserUseCase {
    private final UserRepository users;
    private final PasswordHasher passwords;

    public RegisterUserUseCase(UserRepository users, PasswordHasher passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    public User execute(String email, String username, String password) {
        validate(email, "email");
        validate(username, "username");
        validatePassword(password);
        if (users.existsByEmail(email, null)) {
            throw new UserException("email", "has already been taken", UserException.ErrorKind.CONFLICT);
        }
        if (users.existsByUsername(username, null)) {
            throw new UserException("username", "has already been taken", UserException.ErrorKind.CONFLICT);
        }
        return users.save(User.create(email, username, passwords.hash(password), null, null));
    }

    static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new UserException("password", "must contain at least 8 characters", UserException.ErrorKind.VALIDATION);
        }
    }

    static void validate(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new UserException(field, "can't be blank", UserException.ErrorKind.VALIDATION);
        }
    }
}
