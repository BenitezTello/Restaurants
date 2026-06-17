package com.tingo.restaurants.application.service;

import com.tingo.restaurants.application.dto.request.CreateScheduleRequest;
import com.tingo.restaurants.application.dto.response.ScheduleResponse;
import com.tingo.restaurants.domain.exception.RestaurantNotFoundException;
import com.tingo.restaurants.infrastructure.persistence.entity.RestaurantEntity;
import com.tingo.restaurants.infrastructure.persistence.entity.ScheduleEntity;
import com.tingo.restaurants.infrastructure.persistence.repository.RestaurantJpaRepository;
import com.tingo.restaurants.infrastructure.persistence.repository.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleJpaRepository scheduleRepository;
    private final RestaurantJpaRepository restaurantRepository;

    public List<ScheduleResponse> findByRestaurant(UUID restaurantId) {
        return scheduleRepository.findByRestaurant_IdOrderByDayOfWeekAsc(restaurantId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** Reemplaza por completo los horarios del restaurante (borra existentes e inserta los nuevos). */
    @Transactional
    public List<ScheduleResponse> updateAll(UUID restaurantId, List<CreateScheduleRequest> requests,
                                            UUID userId, boolean isAdmin) {
        RestaurantEntity restaurant = loadAndAuthorize(restaurantId, userId, isAdmin);

        scheduleRepository.deleteByRestaurant_Id(restaurantId);
        scheduleRepository.flush(); // asegura los DELETE antes de los INSERT

        List<ScheduleEntity> toSave = requests.stream()
                .map(r -> ScheduleEntity.builder()
                        .restaurant(restaurant)
                        .dayOfWeek(r.getDayOfWeek())
                        .openingTime(r.getOpeningTime())
                        .closingTime(r.getClosingTime())
                        .closed(r.isClosed())
                        .build())
                .collect(Collectors.toList());

        List<ScheduleEntity> saved = scheduleRepository.saveAll(toSave);
        log.info("Horarios reemplazados para restaurante {}: {} días", restaurantId, saved.size());
        return saved.stream()
                .sorted(Comparator.comparing(ScheduleEntity::getDayOfWeek))
                .map(this::toResponse).collect(Collectors.toList());
    }

    /** Actualiza (o crea) el horario de un día específico. */
    @Transactional
    public ScheduleResponse updateDay(UUID restaurantId, DayOfWeek day, CreateScheduleRequest request,
                                      UUID userId, boolean isAdmin) {
        RestaurantEntity restaurant = loadAndAuthorize(restaurantId, userId, isAdmin);

        ScheduleEntity entity = scheduleRepository.findByRestaurant_IdAndDayOfWeek(restaurantId, day)
                .orElseGet(() -> ScheduleEntity.builder().restaurant(restaurant).dayOfWeek(day).build());
        entity.setOpeningTime(request.getOpeningTime());
        entity.setClosingTime(request.getClosingTime());
        entity.setClosed(request.isClosed());

        log.info("Horario del {} actualizado para restaurante {}", day, restaurantId);
        return toResponse(scheduleRepository.save(entity));
    }

    private RestaurantEntity loadAndAuthorize(UUID restaurantId, UUID userId, boolean isAdmin) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        if (!isAdmin && !restaurant.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("No puedes modificar los horarios de este restaurante");
        }
        return restaurant;
    }

    private ScheduleResponse toResponse(ScheduleEntity s) {
        return ScheduleResponse.builder()
                .id(s.getId())
                .dayOfWeek(s.getDayOfWeek())
                .openingTime(s.getOpeningTime())
                .closingTime(s.getClosingTime())
                .isClosed(s.isClosed())
                .build();
    }
}
