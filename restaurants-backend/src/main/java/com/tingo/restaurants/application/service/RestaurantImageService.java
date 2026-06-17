package com.tingo.restaurants.application.service;

import com.tingo.restaurants.application.dto.request.CreateImageRequest;
import com.tingo.restaurants.application.dto.request.ReorderImageRequest;
import com.tingo.restaurants.application.dto.response.RestaurantImageResponse;
import com.tingo.restaurants.domain.exception.DomainException;
import com.tingo.restaurants.domain.exception.RestaurantNotFoundException;
import com.tingo.restaurants.infrastructure.persistence.entity.RestaurantEntity;
import com.tingo.restaurants.infrastructure.persistence.entity.RestaurantImageEntity;
import com.tingo.restaurants.infrastructure.persistence.repository.RestaurantImageJpaRepository;
import com.tingo.restaurants.infrastructure.persistence.repository.RestaurantJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantImageService {

    private final RestaurantImageJpaRepository imageRepository;
    private final RestaurantJpaRepository restaurantRepository;

    public List<RestaurantImageResponse> findByRestaurant(UUID restaurantId) {
        return imageRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantImageResponse addImage(UUID restaurantId, CreateImageRequest request,
                                            UUID userId, boolean isAdmin) {
        RestaurantEntity restaurant = authorize(restaurantId, userId, isAdmin);

        int order = request.getDisplayOrder() != null
                ? request.getDisplayOrder()
                : imageRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId).size();

        RestaurantImageEntity entity = RestaurantImageEntity.builder()
                .restaurantId(restaurantId)
                .url(request.getUrl())
                .caption(request.getCaption())
                .displayOrder(order)
                .build();

        RestaurantImageEntity saved = imageRepository.save(entity);
        syncCoverImage(restaurant);
        log.info("Imagen agregada al restaurante {}: {}", restaurantId, saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void deleteImage(UUID restaurantId, UUID imageId, UUID userId, boolean isAdmin) {
        RestaurantEntity restaurant = authorize(restaurantId, userId, isAdmin);

        RestaurantImageEntity image = imageRepository.findById(imageId)
                .orElseThrow(() -> new DomainException("Imagen no encontrada", "IMAGE_NOT_FOUND") {});
        if (!image.getRestaurantId().equals(restaurantId)) {
            throw new DomainException("La imagen no pertenece a este restaurante", "IMAGE_MISMATCH") {};
        }
        imageRepository.delete(image);
        imageRepository.flush();
        syncCoverImage(restaurant);
        log.info("Imagen {} eliminada del restaurante {}", imageId, restaurantId);
    }

    @Transactional
    public List<RestaurantImageResponse> reorder(UUID restaurantId, List<ReorderImageRequest> items,
                                                 UUID userId, boolean isAdmin) {
        RestaurantEntity restaurant = authorize(restaurantId, userId, isAdmin);

        Map<UUID, Integer> orderById = items.stream()
                .collect(Collectors.toMap(ReorderImageRequest::getId, ReorderImageRequest::getDisplayOrder));

        List<RestaurantImageEntity> images = imageRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurantId);
        images.forEach(img -> {
            Integer newOrder = orderById.get(img.getId());
            if (newOrder != null) img.setDisplayOrder(newOrder);
        });
        imageRepository.saveAll(images);
        imageRepository.flush();
        syncCoverImage(restaurant);

        return images.stream()
                .sorted(Comparator.comparingInt(RestaurantImageEntity::getDisplayOrder))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * La portada del restaurante es siempre la imagen #0 (la de menor displayOrder).
     * Se sincroniza coverImageUrl con la primera foto tras cada cambio de la galería,
     * para que la tarjeta del listado y el header del detalle muestren esa portada.
     */
    private void syncCoverImage(RestaurantEntity restaurant) {
        List<RestaurantImageEntity> images =
                imageRepository.findByRestaurantIdOrderByDisplayOrderAsc(restaurant.getId());
        String cover = images.isEmpty() ? null : images.get(0).getUrl();
        restaurant.setCoverImageUrl(cover);
        restaurantRepository.save(restaurant);
    }

    private RestaurantEntity authorize(UUID restaurantId, UUID userId, boolean isAdmin) {
        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .filter(e -> e.getDeletedAt() == null)
                .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));
        if (!isAdmin && !restaurant.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("No puedes modificar las imágenes de este restaurante");
        }
        return restaurant;
    }

    private RestaurantImageResponse toResponse(RestaurantImageEntity img) {
        return RestaurantImageResponse.builder()
                .id(img.getId())
                .restaurantId(img.getRestaurantId())
                .url(img.getUrl())
                .caption(img.getCaption())
                .displayOrder(img.getDisplayOrder())
                .createdAt(img.getCreatedAt())
                .build();
    }
}
