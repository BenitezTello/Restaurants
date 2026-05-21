package com.tingo.restaurants.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SearchRestaurantRequest {

    private String name;
    private String city;
    private String category;

    // Búsqueda geográfica
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Double radiusKm = 5.0;

    // Filtros de disponibilidad
    private DayOfWeek dayOfWeek;
    private LocalTime time;

    // Filtros de capacidad
    private Integer minCapacity;

    // Filtros de características
    private Boolean acceptsReservations;
    private Boolean acceptsEvents;
    private Boolean hasParking;
    private Boolean hasWifi;

    // Búsqueda por evento cercano
    private UUID nearEventId;

    // Calificación mínima
    private Double minRating;
}
