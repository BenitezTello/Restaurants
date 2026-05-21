package com.tingo.restaurants.infrastructure.persistence.repository;

import com.tingo.restaurants.infrastructure.persistence.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, UUID> {
    List<MenuEntity> findByRestaurantIdAndDeletedAtIsNull(UUID restaurantId);
    List<MenuEntity> findByRestaurantIdAndActiveIsTrueAndDeletedAtIsNull(UUID restaurantId);
}
