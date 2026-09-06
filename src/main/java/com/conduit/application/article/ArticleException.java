package com.conduit.application.article;

public class ArticleException extends RuntimeException {
    private final String field;
    private final ErrorKind kind;

    public ArticleException(String field, String message, ErrorKind kind) {
        super(message);
        this.field = field;
        this.kind = kind;
    }

    public String field() { return field; }
    public ErrorKind kind() { return kind; }

    public enum ErrorKind { VALIDATION, UNAUTHORIZED, FORBIDDEN, NOT_FOUND }
}