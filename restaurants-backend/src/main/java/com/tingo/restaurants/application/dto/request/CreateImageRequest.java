package com.tingo.restaurants.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateImageRequest {

    @NotBlank(message = "La URL de la imagen es obligatoria")
    @Size(max = 500, message = "La URL no puede exceder 500 caracteres")
    private String url;

    @Size(max = 200, message = "El texto descriptivo no puede exceder 200 caracteres")
    private String caption;

    /** Posición en la galería. Si es null, se agrega al final. */
    private Integer displayOrder;
}
