package com.tingo.restaurants.domain.repository;

import com.tingo.restaurants.domain.model.Dish;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DishRepository {
    Dish save(Dish dish);
    Optional<Dish> findById(UUID id);
    List<Dish> findByMenuId(UUID menuId);
    List<Dish> findByRestaurantId(UUID restaurantId);
    void deleteById(UUID id);
}
