package com.tingo.restaurants.infrastructure.persistence.repository;

import com.tingo.restaurants.infrastructure.persistence.entity.ScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleJpaRepository extends JpaRepository<ScheduleEntity, UUID> {

    List<ScheduleEntity> findByRestaurant_IdOrderByDayOfWeekAsc(UUID restaurantId);

    Optional<ScheduleEntity> findByRestaurant_IdAndDayOfWeek(UUID restaurantId, DayOfWeek dayOfWeek);

    void deleteByRestaurant_Id(UUID restaurantId);
}
