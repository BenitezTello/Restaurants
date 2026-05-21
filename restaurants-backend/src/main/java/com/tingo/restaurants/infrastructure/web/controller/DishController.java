package com.tingo.restaurants.infrastructure.web.controller;

import com.tingo.restaurants.application.dto.request.CreateDishRequest;
import com.tingo.restaurants.application.dto.response.ApiResponse;
import com.tingo.restaurants.application.dto.response.DishResponse;
import com.tingo.restaurants.application.service.DishService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/dishes")
@RequiredArgsConstructor
@Tag(name = "Platos", description = "Gestión de platos del menú")
public class DishController {

    private final DishService dishService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Agregar plato a un menú")
    public ResponseEntity<ApiResponse<DishResponse>> create(@Valid @RequestBody CreateDishRequest request) {
        DishResponse response = dishService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Plato agregado exitosamente", response));
    }

    @GetMapping("/menu/{menuId}")
    @Operation(summary = "Obtener platos de un menú (público)")
    public ResponseEntity<ApiResponse<List<DishResponse>>> findByMenu(@PathVariable UUID menuId) {
        return ResponseEntity.ok(ApiResponse.ok(dishService.findByMenu(menuId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar plato")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        dishService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Plato eliminado", null));
    }
}
