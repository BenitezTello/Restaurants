package com.tingo.restaurants.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    private UUID id;
    private UUID restaurantId;
    private DayOfWeek dayOfWeek;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private boolean isClosed;

    public boolean isOpenAt(LocalTime time) {
        if (isClosed) return false;
        return !time.isBefore(openingTime) && !time.isAfter(closingTime);
    }
}
