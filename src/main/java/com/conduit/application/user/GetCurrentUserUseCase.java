package com.conduit.application.user;

import com.conduit.domain.user.User;

import java.util.UUID;

public class GetCurrentUserUseCase {
    private final UserRepository users;

    public GetCurrentUserUseCase(UserRepository users) {
        this.users = users;
    }

    public User execute(UUID userId) {
        if (userId == null) {
            throw new UserException("token", "is missing", UserException.ErrorKind.UNAUTHORIZED);
        }
        return users.findById(userId)
                .orElseThrow(() -> new UserException("token", "is invalid", UserException.ErrorKind.UNAUTHORIZED));
    }
}
