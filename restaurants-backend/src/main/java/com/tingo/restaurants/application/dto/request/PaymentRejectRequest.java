package com.tingo.restaurants.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentRejectRequest {
    @NotBlank(message = "El motivo de rechazo es requerido")
    private String reason;
}
