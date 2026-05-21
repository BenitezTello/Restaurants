package com.tingo.restaurants.application.mapper;

import com.tingo.restaurants.application.dto.response.ReservationResponse;
import com.tingo.restaurants.domain.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    @Mapping(target = "restaurantName", ignore = true)
    @Mapping(target = "isEventRelated", source = "reservation.eventRelated")
    ReservationResponse toResponse(Reservation reservation);
}
