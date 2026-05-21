package com.tingo.restaurants.infrastructure.web.controller;

import com.tingo.restaurants.application.dto.request.CreateMenuRequest;
import com.tingo.restaurants.application.dto.response.ApiResponse;
import com.tingo.restaurants.application.dto.response.MenuResponse;
import com.tingo.restaurants.application.service.MenuService;
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
@RequestMapping("/v1/menus")
@RequiredArgsConstructor
@Tag(name = "Menús", description = "Gestión de menús del restaurante")
public class MenuController {

    private final MenuService menuService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nuevo menú")
    public ResponseEntity<ApiResponse<MenuResponse>> create(@Valid @RequestBody CreateMenuRequest request) {
        MenuResponse response = menuService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Menú creado exitosamente", response));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Obtener todos los menús de un restaurante (público)")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> findByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.ok(menuService.findByRestaurant(restaurantId)));
    }

    @GetMapping("/restaurant/{restaurantId}/active")
    @Operation(summary = "Obtener menús activos de un restaurante (público)")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> findActiveByRestaurant(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.ok(menuService.findActiveByRestaurant(restaurantId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar menú")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        menuService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Menú eliminado", null));
    }
}
