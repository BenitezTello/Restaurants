import Link from 'next/link';
import Image from 'next/image';
import { MapPin, Star, Users, Wifi, Car, Clock } from 'lucide-react';
import { cn } from '@/utils/cn';
import { formatDistance, formatRating, STATUS_COLORS, STATUS_LABELS } from '@/utils/formatters';
import type { Restaurant } from '@/types/restaurant';

interface Props {
  restaurant: Restaurant;
  showDistance?: boolean;
}

export function RestaurantCard({ restaurant, showDistance }: Props) {
  return (
    <Link href={`/restaurants/${restaurant.slug}`} className="group block">
      <article className="rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-sm hover:shadow-md transition-all duration-300 h-full flex flex-col">
        {/* Cover Image */}
        <div className="relative h-48 bg-gradient-to-br from-orange-100 to-orange-200 overflow-hidden">
          {restaurant.coverImageUrl ? (
            <Image
              src={restaurant.coverImageUrl}
              alt={restaurant.name}
              fill
              className="object-cover group-hover:scale-105 transition-transform duration-300"
            />
          ) : (
            <div className="absolute inset-0 flex items-center justify-center text-orange-300 text-5xl">
              🍽️
            </div>
          )}
          {/* Status Badge */}
          <span className={cn(
            'absolute top-3 left-3 inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium',
            STATUS_COLORS[restaurant.status]
          )}>
            {STATUS_LABELS[restaurant.status]}
          </span>
          {/* Distance badge */}
          {showDistance && restaurant.distanceKm !== undefined && (
            <span className="absolute top-3 right-3 inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-xs font-medium bg-black/60 text-white backdrop-blur-sm">
              <MapPin className="h-3 w-3" />
              {formatDistance(restaurant.distanceKm)}
            </span>
          )}
        </div>

        {/* Content */}
        <div className="p-5 flex-1 flex flex-col">
          {/* Logo + Name */}
          <div className="flex items-start gap-3 mb-3">
            {restaurant.logoUrl && (
              <div className="relative h-10 w-10 rounded-xl overflow-hidden flex-shrink-0 bg-gray-100">
                <Image src={restaurant.logoUrl} alt="" fill className="object-cover" />
              </div>
            )}
            <div>
              <h3 className="font-display font-semibold text-gray-900 group-hover:text-orange-600 transition-colors leading-tight">
                {restaurant.name}
              </h3>
              <div className="flex items-center gap-1 text-xs text-gray-500 mt-0.5">
                <MapPin className="h-3 w-3" />
                {restaurant.district || restaurant.city}
              </div>
            </div>
          </div>

          {/* Categories */}
          {restaurant.categories?.length > 0 && (
            <div className="flex flex-wrap gap-1.5 mb-3">
              {restaurant.categories.slice(0, 3).map((cat) => (
                <span key={cat} className="px-2 py-0.5 bg-orange-50 text-orange-700 text-xs rounded-full font-medium">
                  {cat}
                </span>
              ))}
            </div>
          )}

          {/* Description */}
          {restaurant.description && (
            <p className="text-xs text-gray-500 line-clamp-2 mb-3 flex-1">
              {restaurant.description}
            </p>
          )}

          {/* Features */}
          <div className="flex items-center gap-3 text-xs text-gray-400 mt-auto pt-3 border-t border-gray-50">
            <div className="flex items-center gap-1">
              <Star className="h-3.5 w-3.5 text-yellow-400 fill-yellow-400" />
              <span className="font-medium text-gray-700">{formatRating(restaurant.avgRating)}</span>
              <span>({restaurant.totalRatings})</span>
            </div>
            <div className="flex items-center gap-1">
              <Users className="h-3.5 w-3.5" />
              {restaurant.totalCapacity}
            </div>
            {restaurant.hasWifi && <Wifi className="h-3.5 w-3.5" />}
            {restaurant.hasParking && <Car className="h-3.5 w-3.5" />}
            {restaurant.acceptsReservations && (
              <span className="ml-auto text-orange-500 font-medium">Reservar</span>
            )}
          </div>
        </div>
      </article>
    </Link>
  );
}

export function RestaurantCardSkeleton() {
  return (
    <div className="rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-sm">
      <div className="h-48 skeleton" />
      <div className="p-5 space-y-3">
        <div className="h-5 skeleton rounded-lg w-3/4" />
        <div className="h-3 skeleton rounded-lg w-1/2" />
        <div className="flex gap-2">
          <div className="h-5 skeleton rounded-full w-20" />
          <div className="h-5 skeleton rounded-full w-16" />
        </div>
        <div className="h-3 skeleton rounded-lg" />
        <div className="h-3 skeleton rounded-lg w-4/5" />
      </div>
    </div>
  );
}
