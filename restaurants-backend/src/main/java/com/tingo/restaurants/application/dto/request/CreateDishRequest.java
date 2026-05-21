package com.tingo.restaurants.application.dto.request;

import com.tingo.restaurants.domain.model.enums.DishCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateDishRequest {

    @NotNull
    private UUID menuId;

    @NotBlank(message = "El nombre del plato es obligatorio")
    @Size(min = 2, max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull(message = "La categoría del plato es obligatoria")
    private DishCategory category;

    @NotNull @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Min(1) @Max(300)
    private Integer preparationTime;

    private Integer calories;
    private String imageUrl;
    private boolean isAvailable = true;
    private boolean isFeatured = false;
    private boolean isVegetarian = false;
    private boolean isVegan = false;
    private boolean isGlutenFree = false;
    private List<String> allergens;
}
