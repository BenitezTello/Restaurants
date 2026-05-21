package com.tingo.restaurants.infrastructure.persistence.adapter;

import com.tingo.restaurants.domain.model.Dish;
import com.tingo.restaurants.domain.model.Menu;
import com.tingo.restaurants.domain.model.enums.DishCategory;
import com.tingo.restaurants.domain.repository.MenuRepository;
import com.tingo.restaurants.infrastructure.persistence.entity.DishEntity;
import com.tingo.restaurants.infrastructure.persistence.entity.MenuEntity;
import com.tingo.restaurants.infrastructure.persistence.entity.RestaurantEntity;
import com.tingo.restaurants.infrastructure.persistence.repository.MenuJpaRepository;
import com.tingo.restaurants.infrastructure.persistence.repository.RestaurantJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MenuRepositoryAdapter implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;
    private final RestaurantJpaRepository restaurantJpaRepository;

    @Override
    public Menu save(Menu menu) {
        MenuEntity entity = toEntity(menu);
        return toDomain(menuJpaRepository.save(entity));
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return menuJpaRepository.findById(id)
                .filter(e -> e.getDeletedAt() == null)
                .map(this::toDomain);
    }

    @Override
    public List<Menu> findByRestaurantId(UUID restaurantId) {
        return menuJpaRepository.findByRestaurantIdAndDeletedAtIsNull(restaurantId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Menu> findActiveByRestaurantId(UUID restaurantId) {
        return menuJpaRepository.findByRestaurantIdAndActiveIsTrueAndDeletedAtIsNull(restaurantId)
                .stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        menuJpaRepository.findById(id).ifPresent(e -> {
            e.softDelete();
            menuJpaRepository.save(e);
        });
    }

    private Menu toDomain(MenuEntity e) {
        List<Dish> dishes = e.getDishes() == null ? List.of() :
                e.getDishes().stream().filter(d -> d.getDeletedAt() == null).map(this::dishToDomain).collect(Collectors.toList());
        return Menu.builder()
                .id(e.getId())
                .restaurantId(e.getRestaurant() != null ? e.getRestaurant().getId() : null)
                .name(e.getName()).description(e.getDescription())
                .isActive(e.isActive()).validFrom(e.getValidFrom()).validUntil(e.getValidUntil())
                .dishes(dishes).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .deletedAt(e.getDeletedAt()).build();
    }

    private Dish dishToDomain(DishEntity e) {
        return Dish.builder()
                .id(e.getId()).menuId(e.getMenu() != null ? e.getMenu().getId() : null)
                .restaurantId(e.getRestaurantId()).name(e.getName()).description(e.getDescription())
                .category(e.getCategory()).price(e.getPrice()).preparationTime(e.getPreparationTime())
                .calories(e.getCalories()).imageUrl(e.getImageUrl())
                .isAvailable(e.isAvailable()).isFeatured(e.isFeatured())
                .isVegetarian(e.isVegetarian()).isVegan(e.isVegan()).isGlutenFree(e.isGlutenFree())
                .allergens(e.getAllergens()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt())
                .build();
    }

    private MenuEntity toEntity(Menu menu) {
        MenuEntity entity = new MenuEntity();
        entity.setId(menu.getId());
        entity.setName(menu.getName());
        entity.setDescription(menu.getDescription());
        entity.setActive(menu.isActive());
        entity.setValidFrom(menu.getValidFrom());
        entity.setValidUntil(menu.getValidUntil());
        if (menu.getRestaurantId() != null) {
            restaurantJpaRepository.findById(menu.getRestaurantId())
                    .ifPresent(entity::setRestaurant);
        }
        return entity;
    }
}
