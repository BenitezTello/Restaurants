package com.tingo.restaurants.infrastructure.persistence.adapter;

import com.tingo.restaurants.domain.model.Dish;
import com.tingo.restaurants.domain.repository.DishRepository;
import com.tingo.restaurants.infrastructure.persistence.entity.DishEntity;
import com.tingo.restaurants.infrastructure.persistence.entity.MenuEntity;
import com.tingo.restaurants.infrastructure.persistence.repository.DishJpaRepository;
import com.tingo.restaurants.infrastructure.persistence.repository.MenuJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DishRepositoryAdapter implements DishRepository {

    private final DishJpaRepository dishJpaRepository;
    private final MenuJpaRepository menuJpaRepository;

    @Override
    public Dish save(Dish dish) {
        return toDomain(dishJpaRepository.save(toEntity(dish)));
    }

    @Override
    public Optional<Dish> findById(UUID id) {
        return dishJpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(this::toDomain);
    }

    @Override
    public List<Dish> findByMenuId(UUID menuId) {
        return dishJpaRepository.findByMenuIdAndDeletedAtIsNull(menuId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Dish> findByRestaurantId(UUID restaurantId) {
        return dishJpaRepository.findByRestaurantIdAndDeletedAtIsNull(restaurantId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        dishJpaRepository.findById(id).ifPresent(e -> {
            e.softDelete();
            dishJpaRepository.save(e);
        });
    }

    private Dish toDomain(DishEntity e) {
        return Dish.builder()
                .id(e.getId())
                .menuId(e.getMenu() != null ? e.getMenu().getId() : null)
                .restaurantId(e.getRestaurantId()).name(e.getName()).description(e.getDescription())
                .category(e.getCategory()).price(e.getPrice()).preparationTime(e.getPreparationTime())
                .calories(e.getCalories()).imageUrl(e.getImageUrl())
                .isAvailable(e.isAvailable()).isFeatured(e.isFeatured())
                .isVegetarian(e.isVegetarian()).isVegan(e.isVegan()).isGlutenFree(e.isGlutenFree())
                .allergens(e.getAllergens()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private DishEntity toEntity(Dish dish) {
        DishEntity entity = new DishEntity();
        entity.setId(dish.getId());
        entity.setRestaurantId(dish.getRestaurantId());
        entity.setName(dish.getName());
        entity.setDescription(dish.getDescription());
        entity.setCategory(dish.getCategory());
        entity.setPrice(dish.getPrice());
        entity.setPreparationTime(dish.getPreparationTime());
        entity.setCalories(dish.getCalories());
        entity.setImageUrl(dish.getImageUrl());
        entity.setAvailable(dish.isAvailable());
        entity.setFeatured(dish.isFeatured());
        entity.setVegetarian(dish.isVegetarian());
        entity.setVegan(dish.isVegan());
        entity.setGlutenFree(dish.isGlutenFree());
        entity.setAllergens(dish.getAllergens());
        if (dish.getMenuId() != null) {
            menuJpaRepository.findById(dish.getMenuId()).ifPresent(entity::setMenu);
        }
        return entity;
    }
}
