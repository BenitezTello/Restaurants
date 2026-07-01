'use client';

import { useState } from 'react';
import { Calendar } from 'lucide-react';
import toast from 'react-hot-toast';
import { useMyReservations, useCancelReservation } from '@/hooks/useReservations';
import { useRatings } from '@/hooks/useRatings';
import { ReservationRow } from '@/components/ui/ReservationRow';
import { RatingModal } from '@/components/ui/RatingModal';
import { ReservationDetailsModal } from '@/components/ui/ReservationDetailsModal';
import type { Reservation } from '@/types/reservation';

export default function ClientReservationsPage() {
  const { data: myReservations, isLoading } = useMyReservations();
  const cancelMutation = useCancelReservation();
  const { createRating, loading: ratingLoading } = useRatings();

  const [reviewModalOpen, setReviewModalOpen] = useState(false);
  const [reviewReservationId, setReviewReservationId] = useState('');

  const [detailsModalOpen, setDetailsModalOpen] = useState(false);
  const [selectedReservation, setSelectedReservation] = useState<Reservation | null>(null);

  const handleCancel = async (id: string) => {
    try {
      await cancelMutation.mutateAsync({ id, reason: 'Cancelada por el cliente' });
      toast.success('Reserva cancelada');
    } catch {
      toast.error('Error al cancelar la reserva');
    }
  };

  const handleOpenReview = (id: string) => {
    setReviewReservationId(id);
    setReviewModalOpen(true);
  };

  const handleViewDetails = (res: Reservation) => {
    setSelectedReservation(res);
    setDetailsModalOpen(true);
  };

  const handleSubmitReview = async (data: any) => {
    try {
      await createRating({
        reservationId: reviewReservationId,
        ...data
      });
      toast.success('Reseña publicada exitosamente');
      setReviewModalOpen(false);
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Error al publicar reseña');
    }
  };

  const reservations = myReservations?.content ?? [];

  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('');

  const filteredAndSortedReservations = reservations
    .filter(res => {
      if (statusFilter && res.status !== statusFilter) return false;
      if (searchQuery) {
        const q = searchQuery.toLowerCase();
        return (
          res.restaurantName?.toLowerCase().includes(q) ||
          res.confirmationCode.toLowerCase().includes(q)
        );
      }
      return true;
    })
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());

  return (
    <div>
      <div className="mb-8">
        <h1 className="font-display text-2xl font-bold text-gray-900">Mis Reservas</h1>
        <p className="text-gray-600 mt-1">Tu historial de reservas y próximas citas</p>
      </div>

      <div className="mb-6 flex flex-col sm:flex-row gap-3">
        <input
          type="text"
          placeholder="Buscar por restaurante o código..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="flex-1 rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
        />
        <select
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
          className="rounded-xl border border-gray-200 px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500 bg-white min-w-[160px]"
        >
          <option value="">Todos los estados</option>
          <option value="PENDING">Pendiente</option>
          <option value="CONFIRMED">Confirmada</option>
          <option value="COMPLETED">Completada</option>
          <option value="CANCELLED">Cancelada</option>
        </select>
      </div>

      {isLoading ? (
        <div className="space-y-3">
          {Array.from({ length: 4 }).map((_, i) => (
            <div key={i} className="h-24 skeleton rounded-2xl" />
          ))}
        </div>
      ) : filteredAndSortedReservations.length === 0 ? (
        <div className="bg-white rounded-2xl border border-gray-100 p-12 text-center">
          <Calendar className="h-12 w-12 mx-auto text-gray-200 mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-1">Sin reservas</h3>
          <p className="text-gray-500">No se encontraron reservas con esos filtros.</p>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredAndSortedReservations.map((res) => (
            <ReservationRow
              key={res.id}
              res={res}
              canManage={false}
              onConfirm={() => { }}
              onComplete={() => { }}
              onNoShow={() => { }}
              onCancel={handleCancel}
              onReview={handleOpenReview}
              onViewDetails={handleViewDetails}
              confirmPending={false}
              completePending={false}
              noShowPending={false}
              cancelPending={cancelMutation.isPending}
            />
          ))}
        </div>
      )}

      <RatingModal
        isOpen={reviewModalOpen}
        onClose={() => setReviewModalOpen(false)}
        onSubmit={handleSubmitReview}
        loading={ratingLoading}
      />

      <ReservationDetailsModal
        isOpen={detailsModalOpen}
        onClose={() => setDetailsModalOpen(false)}
        reservation={selectedReservation}
      />
    </div>
  );
}
