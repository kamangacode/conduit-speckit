package com.conduit.api.http;

import com.conduit.application.user.GetCurrentUserUseCase;
import com.conduit.application.user.LoginUserUseCase;
import com.conduit.application.user.RegisterUserUseCase;
import com.conduit.application.user.TokenService;
import com.conduit.application.user.UpdateCurrentUserUseCase;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class UserController {
    private final RegisterUserUseCase register;
    private final LoginUserUseCase login;
    private final GetCurrentUserUseCase current;
    private final UpdateCurrentUserUseCase update;
    private final TokenService tokens;

    public UserController(RegisterUserUseCase register, LoginUserUseCase login,
                          GetCurrentUserUseCase current, UpdateCurrentUserUseCase update,
                          TokenService tokens) {
        this.register = register;
        this.login = login;
        this.current = current;
        this.update = update;
        this.tokens = tokens;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseMapper.UserResponse> register(@RequestBody JsonNode body) {
        JsonNode user = requiredUser(body);
        var created = register.execute(text(user, "email"), text(user, "username"), text(user, "password"));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponseMapper.toResponse(created, tokens));
    }

    @PostMapping("/users/login")
    public UserResponseMapper.UserResponse login(@RequestBody JsonNode body) {
        JsonNode user = requiredUser(body);
        return UserResponseMapper.toResponse(login.execute(text(user, "email"), text(user, "password")), tokens);
    }

    @GetMapping("/user")
    public UserResponseMapper.UserResponse current(HttpServletRequest request) {
        return UserResponseMapper.toResponse(current.execute(userId(request)), tokens);
    }

    @PutMapping("/user")
    public UserResponseMapper.UserResponse update(@RequestBody JsonNode body, HttpServletRequest request) {
        JsonNode user = requiredUser(body);
        var command = new UpdateCurrentUserUseCase.UpdateCommand(
                user.has("email"), nullableText(user, "email"),
                user.has("username"), nullableText(user, "username"),
                user.has("password"), nullableText(user, "password"),
                user.has("bio"), nullableText(user, "bio"),
                user.has("image"), nullableText(user, "image"));
        return UserResponseMapper.toResponse(update.execute(userId(request), command), tokens);
    }

    private static UUID userId(HttpServletRequest request) {
        Object value = request.getAttribute(TokenAuthenticationFilter.CURRENT_USER_ID);
        if (value instanceof UUID id) return id;
        throw new com.conduit.application.user.UserException("token", "is missing", com.conduit.application.user.UserException.ErrorKind.UNAUTHORIZED);
    }

    private static JsonNode requiredUser(JsonNode body) {
        if (body == null || !body.has("user") || !body.get("user").isObject()) {
            throw new com.conduit.application.user.UserException("user", "is required", com.conduit.application.user.UserException.ErrorKind.VALIDATION);
        }
        return body.get("user");
    }

    private static String text(JsonNode node, String field) {
        if (!node.has(field) || node.get(field).isNull()) return null;
        return node.get(field).asText();
    }

    private static String nullableText(JsonNode node, String field) {
        if (!node.has(field)) return null;
        return node.get(field).isNull() ? null : node.get(field).asText();
    }
}
