package com.conduit.application.user;

import java.util.UUID;

public interface TokenService {
    String issue(UUID userId);
    UUID subject(String token);
}
