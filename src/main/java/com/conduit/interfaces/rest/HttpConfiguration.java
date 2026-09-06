package com.conduit.interfaces.rest;

import com.conduit.application.user.TokenService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpConfiguration {
  @Bean
  FilterRegistrationBean<ContentTypeFilter> contentTypeFilter() {
    FilterRegistrationBean<ContentTypeFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new ContentTypeFilter());
    registration.addUrlPatterns("/api/*");
    registration.setOrder(2);
    return registration;
  }

  @Bean
  FilterRegistrationBean<TokenAuthenticationFilter> tokenAuthenticationFilter(TokenService tokens) {
    FilterRegistrationBean<TokenAuthenticationFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new TokenAuthenticationFilter(tokens));
    registration.addUrlPatterns("/api/*");
    registration.setOrder(1);
    return registration;
  }
}
