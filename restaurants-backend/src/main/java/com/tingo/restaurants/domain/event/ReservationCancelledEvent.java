package com.tingo.restaurants.domain.event;

import com.tingo.restaurants.domain.model.Reservation;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ReservationCancelledEvent extends DomainEvent {

    private final UUID reservationId;
    private final UUID restaurantId;
    private final String confirmationCode;
    private final String cancellationReason;

    public ReservationCancelledEvent(Reservation reservation) {
        super("RESERVATION_CANCELLED");
        this.reservationId = reservation.getId();
        this.restaurantId = reservation.getRestaurantId();
        this.confirmationCode = reservation.getConfirmationCode();
        this.cancellationReason = reservation.getCancellationReason();
    }
}
