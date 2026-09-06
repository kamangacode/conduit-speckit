package com.conduit.api.http;

import com.conduit.application.article.ArticleException;
import com.conduit.application.user.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(ArticleException.class)
    ResponseEntity<Map<String, Map<String, List<String>>>> handle(ArticleException exception) {
        int status = switch (exception.kind()) {
            case UNAUTHORIZED -> 401;
            case FORBIDDEN -> 403;
            case NOT_FOUND -> 404;
            case VALIDATION -> 422;
        };
        return ResponseEntity.status(status).body(Map.of("errors", Map.of(exception.field(), List.of(exception.getMessage()))));
    }

    @ExceptionHandler(UserException.class)
    ResponseEntity<Map<String, Map<String, List<String>>>> handle(UserException exception) {
        int status = switch (exception.kind()) {
            case CONFLICT -> 409;
            case UNAUTHORIZED -> 401;
            case NOT_FOUND -> 404;
            case VALIDATION -> 422;
        };
        return ResponseEntity.status(status).body(Map.of("errors", Map.of(exception.field(), List.of(exception.getMessage()))));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Map<String, Map<String, List<String>>>> handleUnexpected(Exception exception) {
        return ResponseEntity.status(422).body(Map.of("errors", Map.of("body", List.of("is invalid"))));
    }
}
