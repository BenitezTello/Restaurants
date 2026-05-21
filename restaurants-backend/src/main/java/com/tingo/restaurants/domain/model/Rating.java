package com.tingo.restaurants.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    private UUID id;
    private UUID restaurantId;
    private UUID userId;
    private UUID reservationId;
    private int score;
    private String comment;
    private Integer foodScore;
    private Integer serviceScore;
    private Integer ambianceScore;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public double calculateDetailedAverage() {
        if (foodScore == null && serviceScore == null && ambianceScore == null) return score;
        double sum = score;
        int count = 1;
        if (foodScore != null) { sum += foodScore; count++; }
        if (serviceScore != null) { sum += serviceScore; count++; }
        if (ambianceScore != null) { sum += ambianceScore; count++; }
        return sum / count;
    }
}
