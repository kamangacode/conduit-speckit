package com.conduit.interfaces.rest.shared;

import com.conduit.application.user.TokenService;
import com.conduit.application.user.UserException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
  public static final String CURRENT_USER_ID =
      TokenAuthenticationFilter.class.getName() + ".currentUserId";
  private final TokenService tokenService;

  public TokenAuthenticationFilter(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Token ")) {
      try {
        UUID userId = tokenService.subject(header.substring("Token ".length()));
        request.setAttribute(CURRENT_USER_ID, userId);
      } catch (UserException ignored) {
        // Protected endpoints map the missing identity to the contract's 401 response.
      }
    }
    filterChain.doFilter(request, response);
  }
}
