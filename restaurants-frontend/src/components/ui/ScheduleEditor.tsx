'use client';

import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Clock, Loader2 } from 'lucide-react';
import { restaurantService } from '@/services/restaurantService';
import { DAY_LABELS } from '@/utils/formatters';
import toast from 'react-hot-toast';
import type { DayOfWeek, ScheduleInput } from '@/types/restaurant';

const DAYS: DayOfWeek[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

const toHHmm = (t?: string | null) => (t ? t.slice(0, 5) : '');

export function ScheduleEditor({ restaurantId }: { restaurantId: string }) {
  const qc = useQueryClient();
  const { data, isLoading } = useQuery({
    queryKey: ['schedules', restaurantId],
    queryFn: () => restaurantService.getSchedules(restaurantId),
    enabled: !!restaurantId,
  });

  const [rows, setRows] = useState<ScheduleInput[]>([]);

  useEffect(() => {
    const byDay = new Map((data ?? []).map((s) => [s.dayOfWeek, s]));
    setRows(
      DAYS.map((day) => {
        const s = byDay.get(day);
        return {
          dayOfWeek: day,
          openingTime: s ? toHHmm(s.openingTime) : '09:00',
          closingTime: s ? toHHmm(s.closingTime) : '22:00',
          isClosed: s ? s.isClosed : false,
        };
      })
    );
  }, [data]);

  const update = (i: number, patch: Partial<ScheduleInput>) =>
    setRows((prev) => prev.map((r, idx) => (idx === i ? { ...r, ...patch } : r)));

  const save = useMutation({
    mutationFn: () =>
      restaurantService.updateSchedules(
        restaurantId,
        rows.map((r) => ({
          dayOfWeek: r.dayOfWeek,
          openingTime: r.isClosed ? null : r.openingTime || null,
          closingTime: r.isClosed ? null : r.closingTime || null,
          isClosed: r.isClosed,
        }))
      ),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['schedules', restaurantId] });
      qc.invalidateQueries({ queryKey: ['restaurants', 'detail', restaurantId] });
      toast.success('Horarios guardados');
    },
    onError: () => toast.error('No se pudieron guardar los horarios'),
  });

  return (
    <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
      <h2 className="font-display text-base font-semibold text-gray-900 mb-4 flex items-center gap-2">
        <Clock className="h-5 w-5 text-orange-500" /> Configurar horarios
      </h2>

      {isLoading ? (
        <div className="space-y-2">{[1, 2, 3].map((i) => <div key={i} className="h-10 skeleton rounded-xl" />)}</div>
      ) : (
        <>
          <div className="space-y-2">
            {rows.map((row, i) => (
              <div key={row.dayOfWeek} className="flex items-center gap-3 flex-wrap sm:flex-nowrap">
                <span className="w-24 text-sm font-medium text-gray-700">{DAY_LABELS[row.dayOfWeek]}</span>

                <label className="inline-flex items-center gap-1.5 text-xs text-gray-500 cursor-pointer w-20">
                  <input
                    type="checkbox"
                    checked={row.isClosed}
                    onChange={(e) => update(i, { isClosed: e.target.checked })}
                    className="rounded border-gray-300 text-orange-500 focus:ring-orange-500"
                  />
                  Cerrado
                </label>

                <input
                  type="time"
                  value={row.openingTime ?? ''}
                  disabled={row.isClosed}
                  onChange={(e) => update(i, { openingTime: e.target.value })}
                  className="border border-gray-200 rounded-xl px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500 disabled:bg-gray-100 disabled:text-gray-300"
                />
                <span className="text-gray-400 text-sm">–</span>
                <input
                  type="time"
                  value={row.closingTime ?? ''}
                  disabled={row.isClosed}
                  onChange={(e) => update(i, { closingTime: e.target.value })}
                  className="border border-gray-200 rounded-xl px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500 disabled:bg-gray-100 disabled:text-gray-300"
                />
              </div>
            ))}
          </div>

          <button
            onClick={() => save.mutate()}
            disabled={save.isPending}
            className="mt-4 inline-flex items-center gap-2 px-5 py-2.5 bg-orange-500 hover:bg-orange-600 disabled:opacity-50 text-white text-sm font-semibold rounded-xl transition-colors"
          >
            {save.isPending && <Loader2 className="h-4 w-4 animate-spin" />} Guardar horarios
          </button>
        </>
      )}
    </div>
  );
}
