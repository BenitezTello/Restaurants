package com.tingo.restaurants.infrastructure.web.controller;

import com.tingo.restaurants.application.dto.request.CreateImageRequest;
import com.tingo.restaurants.application.dto.request.ReorderImageRequest;
import com.tingo.restaurants.application.dto.response.ApiResponse;
import com.tingo.restaurants.application.dto.response.RestaurantImageResponse;
import com.tingo.restaurants.application.service.RestaurantImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Imágenes", description = "Galería de fotos de restaurantes")
public class RestaurantImageController {

    private final RestaurantImageService imageService;

    @GetMapping("/{restaurantId}/images")
    @Operation(summary = "Obtener galería de fotos de un restaurante (público)")
    public ResponseEntity<ApiResponse<List<RestaurantImageResponse>>> getImages(
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.ok(imageService.findByRestaurant(restaurantId)));
    }

    @PostMapping("/{restaurantId}/images")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Agregar una foto a la galería (por URL)")
    public ResponseEntity<ApiResponse<RestaurantImageResponse>> addImage(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody CreateImageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        RestaurantImageResponse response = imageService.addImage(restaurantId, request, userId, isAdmin(userDetails));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Foto agregada", response));
    }

    @DeleteMapping("/{restaurantId}/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar una foto de la galería")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @PathVariable UUID restaurantId,
            @PathVariable UUID imageId,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        imageService.deleteImage(restaurantId, imageId, userId, isAdmin(userDetails));
        return ResponseEntity.ok(ApiResponse.ok("Foto eliminada", null));
    }

    @PatchMapping("/{restaurantId}/images/reorder")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reordenar las fotos de la galería")
    public ResponseEntity<ApiResponse<List<RestaurantImageResponse>>> reorder(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody List<ReorderImageRequest> items,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Galería reordenada",
                imageService.reorder(restaurantId, items, userId, isAdmin(userDetails))));
    }

    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
