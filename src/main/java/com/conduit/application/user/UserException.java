package com.conduit.application.user;

public class UserException extends RuntimeException {
    private final String field;
    private final ErrorKind kind;

    public UserException(String field, String message, ErrorKind kind) {
        super(message);
        this.field = field;
        this.kind = kind;
    }

    public String field() { return field; }
    public ErrorKind kind() { return kind; }

    public enum ErrorKind { VALIDATION, CONFLICT, UNAUTHORIZED, NOT_FOUND }
}
