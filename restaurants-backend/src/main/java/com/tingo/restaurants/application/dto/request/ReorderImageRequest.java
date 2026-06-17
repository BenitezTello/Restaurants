package com.tingo.restaurants.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ReorderImageRequest {

    @NotNull
    private UUID id;

    @NotNull
    private Integer displayOrder;
}
