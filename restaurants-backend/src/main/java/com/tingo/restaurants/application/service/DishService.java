package com.tingo.restaurants.application.service;

import com.tingo.restaurants.application.dto.request.CreateDishRequest;
import com.tingo.restaurants.application.dto.response.DishResponse;
import com.tingo.restaurants.domain.exception.DomainException;
import com.tingo.restaurants.domain.model.Dish;
import com.tingo.restaurants.domain.repository.DishRepository;
import com.tingo.restaurants.domain.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DishService {

    private final DishRepository dishRepository;
    private final MenuRepository menuRepository;

    @Transactional
    public DishResponse create(CreateDishRequest request) {
        var menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new DomainException("Menú no encontrado", "MENU_NOT_FOUND") {});

        Dish dish = Dish.builder()
                .id(UUID.randomUUID())
                .menuId(request.getMenuId())
                .restaurantId(menu.getRestaurantId())
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .preparationTime(request.getPreparationTime())
                .calories(request.getCalories())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.isAvailable())
                .isFeatured(request.isFeatured())
                .isVegetarian(request.isVegetarian())
                .isVegan(request.isVegan())
                .isGlutenFree(request.isGlutenFree())
                .allergens(request.getAllergens())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Dish saved = dishRepository.save(dish);
        log.info("Plato creado: {} en menú: {}", saved.getName(), request.getMenuId());
        return toResponse(saved);
    }

    public List<DishResponse> findByMenu(UUID menuId) {
        return dishRepository.findByMenuId(menuId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id) {
        dishRepository.deleteById(id);
    }

    private DishResponse toResponse(Dish d) {
        return DishResponse.builder()
                .id(d.getId()).name(d.getName()).description(d.getDescription())
                .category(d.getCategory()).price(d.getPrice())
                .preparationTime(d.getPreparationTime()).calories(d.getCalories())
                .imageUrl(d.getImageUrl()).isAvailable(d.isAvailable()).isFeatured(d.isFeatured())
                .isVegetarian(d.isVegetarian()).isVegan(d.isVegan()).isGlutenFree(d.isGlutenFree())
                .allergens(d.getAllergens()).build();
    }
}
