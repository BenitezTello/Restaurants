package com.tingo.restaurants.domain.model;

import com.tingo.restaurants.domain.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    private UUID id;
    private UUID restaurantId;
    private UUID customerId;
    private UUID tableId;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int partySize;
    private ReservationStatus status;
    private String notes;
    private String specialRequests;
    private String confirmationCode;

    private UUID relatedEventId;
    private String relatedEventName;
    private boolean isEventRelated;

    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isPending() {
        return status == ReservationStatus.PENDING;
    }

    public boolean isCancellable() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
    }

    public boolean isConfirmable() {
        return status == ReservationStatus.PENDING;
    }

    public Reservation confirm() {
        if (!isConfirmable()) {
            throw new IllegalStateException("La reserva en estado " + status + " no puede ser confirmada");
        }
        return Reservation.builder()
                .id(this.id)
                .restaurantId(this.restaurantId)
                .customerId(this.customerId)
                .tableId(this.tableId)
                .customerName(this.customerName)
                .customerEmail(this.customerEmail)
                .customerPhone(this.customerPhone)
                .reservationDate(this.reservationDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .partySize(this.partySize)
                .status(ReservationStatus.CONFIRMED)
                .notes(this.notes)
                .specialRequests(this.specialRequests)
                .confirmationCode(this.confirmationCode)
                .relatedEventId(this.relatedEventId)
                .relatedEventName(this.relatedEventName)
                .isEventRelated(this.isEventRelated)
                .confirmedAt(LocalDateTime.now())
                .cancelledAt(this.cancelledAt)
                .cancellationReason(this.cancellationReason)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Reservation cancel(String reason) {
        if (!isCancellable()) {
            throw new IllegalStateException("La reserva en estado " + status + " no puede ser cancelada");
        }
        return Reservation.builder()
                .id(this.id)
                .restaurantId(this.restaurantId)
                .customerId(this.customerId)
                .tableId(this.tableId)
                .customerName(this.customerName)
                .customerEmail(this.customerEmail)
                .customerPhone(this.customerPhone)
                .reservationDate(this.reservationDate)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .partySize(this.partySize)
                .status(ReservationStatus.CANCELLED)
                .notes(this.notes)
                .specialRequests(this.specialRequests)
                .confirmationCode(this.confirmationCode)
                .relatedEventId(this.relatedEventId)
                .relatedEventName(this.relatedEventName)
                .isEventRelated(this.isEventRelated)
                .confirmedAt(this.confirmedAt)
                .cancelledAt(LocalDateTime.now())
                .cancellationReason(reason)
                .createdAt(this.createdAt)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Reservation complete() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Solo se puede completar una reserva CONFIRMED (estado actual: " + status + ")");
        }
        return this.toBuilder()
                .status(ReservationStatus.COMPLETED)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Reservation markNoShow() {
        if (status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Solo se puede marcar no-show desde CONFIRMED (estado actual: " + status + ")");
        }
        return this.toBuilder()
                .status(ReservationStatus.NO_SHOW)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
