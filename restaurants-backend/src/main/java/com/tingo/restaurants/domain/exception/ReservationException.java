package com.tingo.restaurants.domain.exception;

public class ReservationException extends DomainException {

    public ReservationException(String message) {
        super(message, "RESERVATION_ERROR");
    }

    public static ReservationException notFound(String confirmationCode) {
        return new ReservationException("Reserva no encontrada con código: " + confirmationCode);
    }

    public static ReservationException capacityExceeded(int requested, int available) {
        return new ReservationException(
            "Capacidad insuficiente. Solicitado: " + requested + ", Disponible: " + available
        );
    }

    public static ReservationException restaurantClosed() {
        return new ReservationException("El restaurante está cerrado en el horario solicitado");
    }

    public static ReservationException invalidStatus(String current, String target) {
        return new ReservationException(
            "No se puede cambiar de estado " + current + " a " + target
        );
    }
}
