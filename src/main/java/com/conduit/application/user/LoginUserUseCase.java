package com.conduit.application.user;

import com.conduit.domain.user.User;

public class LoginUserUseCase {
    private final UserRepository users;
    private final PasswordHasher passwords;

    public LoginUserUseCase(UserRepository users, PasswordHasher passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    public User execute(String email, String password) {
        RegisterUserUseCase.validate(email, "email");
        RegisterUserUseCase.validate(password, "password");
        User user = users.findByEmail(email)
                .orElseThrow(() -> new UserException("credentials", "invalid", UserException.ErrorKind.UNAUTHORIZED));
        if (!passwords.matches(password, user.passwordHash())) {
            throw new UserException("credentials", "invalid", UserException.ErrorKind.UNAUTHORIZED);
        }
        return user;
    }
}
