package com.conduit.application.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUseCaseConfiguration {
    @Bean RegisterUserUseCase registerUserUseCase(UserRepository users, PasswordHasher passwords) {
        return new RegisterUserUseCase(users, passwords);
    }
    @Bean LoginUserUseCase loginUserUseCase(UserRepository users, PasswordHasher passwords) {
        return new LoginUserUseCase(users, passwords);
    }
    @Bean GetCurrentUserUseCase getCurrentUserUseCase(UserRepository users) {
        return new GetCurrentUserUseCase(users);
    }
    @Bean UpdateCurrentUserUseCase updateCurrentUserUseCase(UserRepository users, PasswordHasher passwords) {
        return new UpdateCurrentUserUseCase(users, passwords);
    }
}
