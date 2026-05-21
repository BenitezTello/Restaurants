package com.tingo.restaurants.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    private UUID id;
    private UUID restaurantId;
    private String name;
    private String description;
    private boolean isActive;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private List<Dish> dishes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public boolean isCurrentlyValid() {
        LocalDate today = LocalDate.now();
        if (validFrom != null && today.isBefore(validFrom)) return false;
        if (validUntil != null && today.isAfter(validUntil)) return false;
        return isActive && deletedAt == null;
    }
}
