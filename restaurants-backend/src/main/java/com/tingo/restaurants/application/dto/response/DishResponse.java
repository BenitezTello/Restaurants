package com.tingo.restaurants.application.dto.response;

import com.tingo.restaurants.domain.model.enums.DishCategory;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class DishResponse {
    private UUID id;
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
}
