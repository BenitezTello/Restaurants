package com.tingo.restaurants.domain.exception;

import java.util.UUID;

public class RestaurantNotFoundException extends DomainException {

    public RestaurantNotFoundException(UUID id) {
        super("Restaurante no encontrado con ID: " + id, "RESTAURANT_NOT_FOUND");
    }

    public RestaurantNotFoundException(String slug) {
        super("Restaurante no encontrado con slug: " + slug, "RESTAURANT_NOT_FOUND");
    }
}
