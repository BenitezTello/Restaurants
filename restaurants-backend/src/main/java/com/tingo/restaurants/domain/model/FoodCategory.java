package com.tingo.restaurants.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodCategory {
    private UUID id;
    private String name;
    private String description;
    private String iconUrl;
    private boolean isActive;
}
