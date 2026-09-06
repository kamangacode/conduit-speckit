package com.conduit.application.user;

import com.conduit.domain.user.User;

import java.util.UUID;

public class UpdateCurrentUserUseCase {
    private final UserRepository users;
    private final PasswordHasher passwords;

    public UpdateCurrentUserUseCase(UserRepository users, PasswordHasher passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    public User execute(UUID userId, UpdateCommand command) {
        User current = users.findById(userId)
                .orElseThrow(() -> new UserException("token", "is invalid", UserException.ErrorKind.UNAUTHORIZED));
        if (command.emailProvided()) {
            RegisterUserUseCase.validate(command.email(), "email");
            if (users.existsByEmail(command.email(), userId)) {
                throw new UserException("email", "has already been taken", UserException.ErrorKind.CONFLICT);
            }
        }
        if (command.usernameProvided()) {
            RegisterUserUseCase.validate(command.username(), "username");
            if (users.existsByUsername(command.username(), userId)) {
                throw new UserException("username", "has already been taken", UserException.ErrorKind.CONFLICT);
            }
        }
        String passwordHash = null;
        if (command.passwordProvided()) {
            RegisterUserUseCase.validatePassword(command.password());
            passwordHash = passwords.hash(command.password());
        }
        return users.save(current.update(
                command.emailProvided() ? command.email() : null,
                command.usernameProvided() ? command.username() : null,
                passwordHash,
                command.bioProvided() ? command.bio() : current.bio(),
                command.imageProvided() ? command.image() : current.image()));
    }

    public record UpdateCommand(
            boolean emailProvided, String email,
            boolean usernameProvided, String username,
            boolean passwordProvided, String password,
            boolean bioProvided, String bio,
            boolean imageProvided, String image) { }
}
