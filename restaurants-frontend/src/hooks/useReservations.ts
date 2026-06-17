import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { reservationService } from '@/services/reservationService';
import type { CreateReservationDto } from '@/types/reservation';

export const RESERVATION_KEYS = {
  all: ['reservations'] as const,
  mine: () => [...RESERVATION_KEYS.all, 'mine'] as const,
  restaurant: (id: string) => [...RESERVATION_KEYS.all, 'restaurant', id] as const,
  code: (code: string) => [...RESERVATION_KEYS.all, 'code', code] as const,
};

export function useMyReservations(page = 0, size = 10) {
  return useQuery({
    queryKey: RESERVATION_KEYS.mine(),
    queryFn: () => reservationService.getMyReservations(page, size),
  });
}

export function useRestaurantReservations(restaurantId: string, page = 0, size = 20) {
  return useQuery({
    queryKey: RESERVATION_KEYS.restaurant(restaurantId),
    queryFn: () => reservationService.getByRestaurant(restaurantId, page, size),
    enabled: !!restaurantId,
  });
}

export function useReservationByCode(code: string) {
  return useQuery({
    queryKey: RESERVATION_KEYS.code(code),
    queryFn: () => reservationService.getByCode(code),
    enabled: !!code,
  });
}

export function useCreateReservation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateReservationDto) => reservationService.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: RESERVATION_KEYS.all });
    },
  });
}

export function useConfirmReservation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => reservationService.confirm(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: RESERVATION_KEYS.all });
    },
  });
}

export function useCancelReservation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, reason }: { id: string; reason?: string }) =>
      reservationService.cancel(id, reason),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: RESERVATION_KEYS.all });
    },
  });
}

export function useCompleteReservation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => reservationService.complete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: RESERVATION_KEYS.all });
    },
  });
}

export function useNoShowReservation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => reservationService.markNoShow(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: RESERVATION_KEYS.all });
    },
  });
}
