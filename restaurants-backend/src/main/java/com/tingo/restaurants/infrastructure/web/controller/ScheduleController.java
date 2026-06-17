package com.tingo.restaurants.infrastructure.web.controller;

import com.tingo.restaurants.application.dto.request.CreateScheduleRequest;
import com.tingo.restaurants.application.dto.response.ApiResponse;
import com.tingo.restaurants.application.dto.response.ScheduleResponse;
import com.tingo.restaurants.application.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/restaurants")
@RequiredArgsConstructor
@Tag(name = "Horarios", description = "Horarios de apertura de los restaurantes")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/{restaurantId}/schedules")
    @Operation(summary = "Obtener los horarios de un restaurante (público)")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getSchedules(@PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.ok(scheduleService.findByRestaurant(restaurantId)));
    }

    @PutMapping("/{restaurantId}/schedules")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reemplazar todos los horarios del restaurante (una entrada por día)")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> updateAll(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody List<CreateScheduleRequest> schedules,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Horarios actualizados",
                scheduleService.updateAll(restaurantId, schedules, userId, isAdmin(userDetails))));
    }

    @PatchMapping("/{restaurantId}/schedules/{day}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RESTAURANTE_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar el horario de un día específico (MONDAY..SUNDAY)")
    public ResponseEntity<ApiResponse<ScheduleResponse>> updateDay(
            @PathVariable UUID restaurantId,
            @PathVariable DayOfWeek day,
            @Valid @RequestBody CreateScheduleRequest schedule,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Horario actualizado",
                scheduleService.updateDay(restaurantId, day, schedule, userId, isAdmin(userDetails))));
    }

    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
