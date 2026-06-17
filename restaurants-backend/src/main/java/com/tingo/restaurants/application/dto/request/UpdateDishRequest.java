package com.tingo.restaurants.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tingo.restaurants.domain.model.enums.DishCategory;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Actualización parcial de un plato: todos los campos son opcionales.
 * El servicio solo aplica los que vengan no-nulos (patch).
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateDishRequest {

    @Size(min = 2, max = 200, message = "El nombre debe tener entre 2 y 200 caracteres")
    private String name;

    @Size(max = 1000)
    private String description;

    private DishCategory category;

    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Min(1) @Max(300)
    private Integer preparationTime;

    private Integer calories;

    private String imageUrl;

    @JsonProperty("isAvailable")
    private Boolean available;

    @JsonProperty("isFeatured")
    private Boolean featured;

    @JsonProperty("isVegetarian")
    private Boolean vegetarian;

    @JsonProperty("isVegan")
    private Boolean vegan;

    @JsonProperty("isGlutenFree")
    private Boolean glutenFree;

    private List<String> allergens;
}
