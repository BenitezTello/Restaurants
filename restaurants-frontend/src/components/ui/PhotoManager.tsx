'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Image as ImageIcon, Trash2, Plus, Loader2 } from 'lucide-react';
import { restaurantService } from '@/services/restaurantService';
import toast from 'react-hot-toast';

export function PhotoManager({ restaurantId }: { restaurantId: string }) {
  const qc = useQueryClient();
  const { data: images, isLoading } = useQuery({
    queryKey: ['images', restaurantId],
    queryFn: () => restaurantService.getImages(restaurantId),
    enabled: !!restaurantId,
  });

  const [url, setUrl] = useState('');
  const [caption, setCaption] = useState('');

  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ['images', restaurantId] });
    qc.invalidateQueries({ queryKey: ['restaurants', 'detail', restaurantId] });
  };

  const add = useMutation({
    mutationFn: () => restaurantService.addImage(restaurantId, { url: url.trim(), caption: caption.trim() || undefined }),
    onSuccess: () => { invalidate(); toast.success('Foto agregada'); setUrl(''); setCaption(''); },
    onError: () => toast.error('No se pudo agregar la foto'),
  });

  const remove = useMutation({
    mutationFn: (imageId: string) => restaurantService.deleteImage(restaurantId, imageId),
    onSuccess: () => { invalidate(); toast.success('Foto eliminada'); },
    onError: () => toast.error('No se pudo eliminar la foto'),
  });

  return (
    <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
      <h2 className="font-display text-base font-semibold text-gray-900 flex items-center gap-2">
        <ImageIcon className="h-5 w-5 text-orange-500" /> Galería de fotos
      </h2>
      <p className="text-xs text-gray-400 mb-4 mt-1">La primera foto (Portada) se usa como imagen principal del restaurante.</p>

      {/* Formulario de agregar */}
      <div className="flex flex-col gap-2 sm:flex-row mb-5">
        <input
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          placeholder="URL de la imagen (https://...) *"
          className="flex-1 border border-gray-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
        />
        <input
          value={caption}
          onChange={(e) => setCaption(e.target.value)}
          placeholder="Descripción (opcional)"
          className="flex-1 border border-gray-200 rounded-xl px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500"
        />
        <button
          onClick={() => add.mutate()}
          disabled={!url.trim() || add.isPending}
          className="inline-flex items-center justify-center gap-1.5 px-4 py-2 bg-orange-500 hover:bg-orange-600 disabled:opacity-50 text-white text-sm font-semibold rounded-xl transition-colors"
        >
          {add.isPending ? <Loader2 className="h-4 w-4 animate-spin" /> : <Plus className="h-4 w-4" />} Agregar
        </button>
      </div>

      {/* Grilla de fotos */}
      {isLoading ? (
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">{[1, 2, 3].map((i) => <div key={i} className="aspect-video skeleton rounded-xl" />)}</div>
      ) : !images?.length ? (
        <div className="text-center py-10 text-gray-400">
          <ImageIcon className="h-10 w-10 mx-auto mb-2 opacity-30" />
          <p className="text-sm">Sin fotos aún. Agrega la primera con una URL pública.</p>
        </div>
      ) : (
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
          {images.map((img, idx) => (
            <div key={img.id} className={`relative group rounded-xl overflow-hidden border ${idx === 0 ? 'border-orange-400 ring-2 ring-orange-200' : 'border-gray-100'}`}>
              {/* eslint-disable-next-line @next/next/no-img-element */}
              <img
                src={img.url}
                alt={img.caption ?? 'Foto del restaurante'}
                className="w-full aspect-video object-cover bg-gray-100"
                onError={(e) => { (e.target as HTMLImageElement).src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="160" height="90"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" font-size="10" fill="%239ca3af" text-anchor="middle" dy=".3em">URL inválida</text></svg>'; }}
              />
              <span className={`absolute top-1.5 left-1.5 text-[10px] font-semibold px-1.5 py-0.5 rounded ${idx === 0 ? 'bg-orange-500 text-white' : 'bg-black/60 text-white'}`}>{idx === 0 ? 'Portada' : `#${img.displayOrder}`}</span>
              <button
                onClick={() => remove.mutate(img.id)}
                disabled={remove.isPending}
                title="Eliminar foto"
                className="absolute top-1.5 right-1.5 p-1.5 bg-white/90 hover:bg-red-500 hover:text-white text-red-500 rounded-lg opacity-0 group-hover:opacity-100 transition-opacity"
              >
                <Trash2 className="h-3.5 w-3.5" />
              </button>
              {img.caption && (
                <p className="absolute bottom-0 inset-x-0 text-[11px] text-white bg-gradient-to-t from-black/70 to-transparent px-2 py-1 truncate">{img.caption}</p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
