package com.conduit.interfaces.rest.shared;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class ContentTypeFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain)
      throws ServletException, IOException {
    chain.doFilter(request, response);
    if (response.getContentType() != null
        && response.getContentType().startsWith("application/json")) {
      response.setContentType("application/json; charset=utf-8");
    }
  }
}
