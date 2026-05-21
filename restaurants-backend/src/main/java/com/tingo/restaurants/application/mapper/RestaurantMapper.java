package com.tingo.restaurants.application.mapper;

import com.tingo.restaurants.application.dto.response.RestaurantResponse;
import com.tingo.restaurants.application.dto.response.ScheduleResponse;
import com.tingo.restaurants.domain.model.FoodCategory;
import com.tingo.restaurants.domain.model.Restaurant;
import com.tingo.restaurants.domain.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    @Mapping(target = "categories", expression = "java(mapCategoryNames(restaurant))")
    @Mapping(target = "distanceKm", ignore = true)
    @Mapping(target = "isAccessible", source = "restaurant.accessible")
    RestaurantResponse toResponse(Restaurant restaurant);

    @Mapping(target = "isClosed", source = "closed")
    ScheduleResponse toScheduleResponse(Schedule schedule);

    default List<String> mapCategoryNames(Restaurant restaurant) {
        if (restaurant.getCategories() == null) return List.of();
        return restaurant.getCategories().stream()
                .map(FoodCategory::getName)
                .collect(Collectors.toList());
    }
}
