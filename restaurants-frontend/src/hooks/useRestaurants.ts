import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { restaurantService } from '@/services/restaurantService';
import type { CreateRestaurantDto } from '@/types/restaurant';

export const RESTAURANT_KEYS = {
  all: ['restaurants'] as const,
  lists: () => [...RESTAURANT_KEYS.all, 'list'] as const,
  list: (filters: object) => [...RESTAURANT_KEYS.lists(), filters] as const,
  detail: (id: string) => [...RESTAURANT_KEYS.all, 'detail', id] as const,
  slug: (slug: string) => [...RESTAURANT_KEYS.all, 'slug', slug] as const,
  nearby: (lat: number, lon: number, r: number) => [...RESTAURANT_KEYS.all, 'nearby', lat, lon, r] as const,
  mine: () => [...RESTAURANT_KEYS.all, 'mine'] as const,
};

export function useRestaurants(page = 0, size = 12) {
  return useQuery({
    queryKey: RESTAURANT_KEYS.list({ page, size }),
    queryFn: () => restaurantService.getAll(page, size),
    staleTime: 1000 * 60 * 5,
  });
}

export function useRestaurant(id: string) {
  return useQuery({
    queryKey: RESTAURANT_KEYS.detail(id),
    queryFn: () => restaurantService.getById(id),
    enabled: !!id,
  });
}

export function useRestaurantBySlug(slug: string) {
  return useQuery({
    queryKey: RESTAURANT_KEYS.slug(slug),
    queryFn: () => restaurantService.getBySlug(slug),
    enabled: !!slug,
  });
}

export function useSearchRestaurants(params: {
  name?: string;
  city?: string;
  category?: string;
  page?: number;
  size?: number;
}) {
  return useQuery({
    queryKey: RESTAURANT_KEYS.list(params),
    queryFn: () => restaurantService.search(params),
    staleTime: 1000 * 60 * 2,
  });
}

export function useNearbyRestaurants(lat: number, lon: number, radiusKm = 5) {
  return useQuery({
    queryKey: RESTAURANT_KEYS.nearby(lat, lon, radiusKm),
    queryFn: () => restaurantService.getNearby(lat, lon, radiusKm),
    enabled: !!lat && !!lon,
    staleTime: 1000 * 60 * 3,
  });
}

export function useMyRestaurants() {
  return useQuery({
    queryKey: RESTAURANT_KEYS.mine(),
    queryFn: () => restaurantService.getMyRestaurants(),
  });
}

export function useCreateRestaurant() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateRestaurantDto) => restaurantService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: RESTAURANT_KEYS.all });
    },
  });
}

export function useDeleteRestaurant() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => restaurantService.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: RESTAURANT_KEYS.all });
    },
  });
}
