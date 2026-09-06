package com.conduit.api.http;

import com.conduit.application.user.TokenService;
import com.conduit.domain.user.User;

public class UserResponseMapper {
    private UserResponseMapper() { }

    public static UserResponse toResponse(User user, TokenService tokenService) {
        return new UserResponse(new UserResponse.Payload(
                user.email(), tokenService.issue(user.id()), user.username(), user.bio(), user.image()));
    }

    public record UserResponse(Payload user) {
        public record Payload(String email, String token, String username, String bio, String image) { }
    }
}
