'use client';

import { Suspense } from 'react';
import { ReservationLookupContent } from '@/components/reservations/ReservationLookupContent';

export default function ReservationLookupPage() {
  return (
    <Suspense fallback={<div className="min-h-screen bg-gray-50 flex items-center justify-center"><div className="h-8 w-8 border-2 border-orange-500 border-t-transparent rounded-full animate-spin" /></div>}>
      <ReservationLookupContent />
    </Suspense>
  );
}
