'use client';

import { Suspense } from 'react';
import { ReservationLookupContent } from '@/components/reservations/ReservationLookupContent';

export default function ProfileSearchReservationPage() {
  return (
    <div className="w-full max-w-4xl mx-auto">
      <Suspense fallback={<div className="flex justify-center p-12"><div className="h-8 w-8 border-2 border-orange-500 border-t-transparent rounded-full animate-spin" /></div>}>
        <ReservationLookupContent isDashboard={true} />
      </Suspense>
    </div>
  );
}
