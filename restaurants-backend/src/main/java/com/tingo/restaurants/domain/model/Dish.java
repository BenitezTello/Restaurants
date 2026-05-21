package com.tingo.restaurants.domain.model;

import com.tingo.restaurants.domain.model.enums.DishCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dish {

    private UUID id;
    private UUID menuId;
    private UUID restaurantId;
    private String name;
    private String description;
    private DishCategory category;
    private BigDecimal price;
    private Integer preparationTime;
    private Integer calories;
    private String imageUrl;
    private boolean isAvailable;
    private boolean isFeatured;
    private boolean isVegetarian;
    private boolean isVegan;
    private boolean isGlutenFree;
    private List<String> allergens;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public boolean isOrderable() {
        return isAvailable && deletedAt == null;
    }
}
