package com.tingo.restaurants.domain.exception;

public class UserAlreadyExistsException extends DomainException {

    public UserAlreadyExistsException(String email) {
        super("Ya existe un usuario con email: " + email, "USER_ALREADY_EXISTS");
    }
}
