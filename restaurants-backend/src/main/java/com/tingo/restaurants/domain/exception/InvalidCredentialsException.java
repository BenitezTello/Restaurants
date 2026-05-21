package com.tingo.restaurants.domain.exception;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Credenciales inválidas", "INVALID_CREDENTIALS");
    }
}
