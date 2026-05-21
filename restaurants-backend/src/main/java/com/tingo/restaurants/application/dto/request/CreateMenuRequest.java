package com.tingo.restaurants.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateMenuRequest {

    @NotNull
    private UUID restaurantId;

    @NotBlank(message = "El nombre del menú es obligatorio")
    @Size(min = 2, max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    private boolean isActive = true;
    private LocalDate validFrom;
    private LocalDate validUntil;
}
