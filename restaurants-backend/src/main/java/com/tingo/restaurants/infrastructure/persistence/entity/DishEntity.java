package com.tingo.restaurants.infrastructure.persistence.entity;

import com.tingo.restaurants.domain.model.enums.DishCategory;
import com.tingo.restaurants.infrastructure.persistence.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dishes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuEntity menu;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DishCategory category;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "preparation_time")
    private Integer preparationTime;

    private Integer calories;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable = true;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private boolean isFeatured = false;

    @Builder.Default
    @Column(name = "is_vegetarian", nullable = false)
    private boolean isVegetarian = false;

    @Builder.Default
    @Column(name = "is_vegan", nullable = false)
    private boolean isVegan = false;

    @Builder.Default
    @Column(name = "is_gluten_free", nullable = false)
    private boolean isGlutenFree = false;

    // Almacenado como texto separado por comas (compatible con cualquier BD)
    @Convert(converter = StringListConverter.class)
    @Column(name = "allergens", columnDefinition = "TEXT")
    private List<String> allergens;
}
