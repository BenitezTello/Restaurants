package com.tingo.restaurants.infrastructure.persistence.repository;

import com.tingo.restaurants.infrastructure.persistence.entity.DishEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DishJpaRepository extends JpaRepository<DishEntity, UUID> {
    List<DishEntity> findByMenuIdAndDeletedAtIsNull(UUID menuId);
    List<DishEntity> findByRestaurantIdAndDeletedAtIsNull(UUID restaurantId);
}
