package com.tingo.restaurants.infrastructure.web.controller;

import com.tingo.restaurants.application.dto.request.CreateReservationRequest;
import com.tingo.restaurants.application.dto.response.ApiResponse;
import com.tingo.restaurants.application.dto.response.PagedResponse;
import com.tingo.restaurants.application.dto.response.ReservationResponse;
import com.tingo.restaurants.application.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Gestión de reservas de restaurantes")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear nueva reserva")
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID customerId = userDetails != null ? UUID.fromString(userDetails.getUsername()) : null;
        ReservationResponse response = reservationService.create(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Reserva creada. Código: " + response.getConfirmationCode(), response));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Buscar reserva por código de confirmación")
    public ResponseEntity<ApiResponse<ReservationResponse>> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.ok(reservationService.findByConfirmationCode(code)));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar reservas de un restaurante")
    public ResponseEntity<ApiResponse<PagedResponse<ReservationResponse>>> findByRestaurant(
            @PathVariable UUID restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate", "startTime"));
        return ResponseEntity.ok(ApiResponse.ok(reservationService.findByRestaurant(restaurantId, pageable)));
    }

    @GetMapping("/my-reservations")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Mis reservas como cliente")
    public ResponseEntity<ApiResponse<PagedResponse<ReservationResponse>>> myReservations(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID customerId = UUID.fromString(userDetails.getUsername());
        PageRequest pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "reservationDate"));
        return ResponseEntity.ok(ApiResponse.ok(reservationService.findByCustomer(customerId, pageable)));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Confirmar reserva")
    public ResponseEntity<ApiResponse<ReservationResponse>> confirm(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Reserva confirmada", reservationService.confirm(id)));
    }

    @PatchMapping("/{id}/cancel")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancelar reserva")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancel(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "Cancelada por el usuario") String reason) {
        return ResponseEntity.ok(ApiResponse.ok("Reserva cancelada", reservationService.cancel(id, reason)));
    }
}
