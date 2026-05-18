package com.codecamp.spring_ddd_example.across.exception;

public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}