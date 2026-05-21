package com.tingo.restaurants.domain.repository;

import com.tingo.restaurants.domain.model.Menu;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

    Menu save(Menu menu);

    Optional<Menu> findById(UUID id);

    List<Menu> findByRestaurantId(UUID restaurantId);

    List<Menu> findActiveByRestaurantId(UUID restaurantId);

    void deleteById(UUID id);
}
