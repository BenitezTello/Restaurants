package com.tingo.restaurants.domain.event;

import com.tingo.restaurants.domain.model.Reservation;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ReservationCreatedEvent extends DomainEvent {

    private final UUID reservationId;
    private final UUID restaurantId;
    private final String confirmationCode;
    private final String customerEmail;
    private final String customerName;

    public ReservationCreatedEvent(Reservation reservation) {
        super("RESERVATION_CREATED");
        this.reservationId = reservation.getId();
        this.restaurantId = reservation.getRestaurantId();
        this.confirmationCode = reservation.getConfirmationCode();
        this.customerEmail = reservation.getCustomerEmail();
        this.customerName = reservation.getCustomerName();
    }
}
