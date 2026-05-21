'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  UtensilsCrossed, Calendar, BarChart3, Tag,
  Settings, LogOut, Home, Users, Map
} from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { cn } from '@/utils/cn';

const navItems = [
  { href: '/dashboard', icon: Home, label: 'Inicio', roles: ['ADMIN', 'RESTAURANTE_OWNER', 'CLIENTE'] },
  { href: '/dashboard/restaurants', icon: UtensilsCrossed, label: 'Restaurantes', roles: ['ADMIN', 'RESTAURANTE_OWNER'] },
  { href: '/dashboard/reservations', icon: Calendar, label: 'Reservas', roles: ['ADMIN', 'RESTAURANTE_OWNER', 'CLIENTE'] },
  { href: '/dashboard/menus', icon: UtensilsCrossed, label: 'Menús', roles: ['ADMIN', 'RESTAURANTE_OWNER'] },
  { href: '/dashboard/promotions', icon: Tag, label: 'Promociones', roles: ['ADMIN', 'RESTAURANTE_OWNER'] },
  { href: '/dashboard/reports', icon: BarChart3, label: 'Reportes', roles: ['ADMIN'] },
  { href: '/dashboard/users', icon: Users, label: 'Usuarios', roles: ['ADMIN'] },
];

export function Sidebar() {
  const pathname = usePathname();
  const { user, logout } = useAuthStore();

  const visible = navItems.filter((item) =>
    user ? item.roles.includes(user.role) : false
  );

  return (
    <aside className="flex h-full w-64 flex-col bg-gray-900 text-white">
      {/* Logo */}
      <div className="flex items-center gap-3 p-6 border-b border-gray-700">
        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-orange-500">
          <UtensilsCrossed className="h-5 w-5 text-white" />
        </div>
        <div>
          <p className="font-display font-semibold text-sm">Restaurants</p>
          <p className="text-xs text-gray-400">Tingo María</p>
        </div>
      </div>

      {/* Nav */}
      <nav className="flex-1 overflow-y-auto p-4 space-y-1">
        {visible.map((item) => {
          const isActive = pathname === item.href || pathname.startsWith(item.href + '/');
          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-orange-500 text-white'
                  : 'text-gray-400 hover:bg-gray-800 hover:text-white'
              )}
            >
              <item.icon className="h-4 w-4 flex-shrink-0" />
              {item.label}
            </Link>
          );
        })}
      </nav>

      {/* User */}
      <div className="border-t border-gray-700 p-4">
        <div className="flex items-center gap-3 mb-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-full bg-orange-500 text-sm font-semibold">
            {user?.fullName.charAt(0).toUpperCase()}
          </div>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium truncate">{user?.fullName}</p>
            <p className="text-xs text-gray-400 truncate">{user?.role}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="flex w-full items-center gap-2 rounded-xl px-3 py-2 text-sm text-gray-400 hover:bg-gray-800 hover:text-white transition-colors"
        >
          <LogOut className="h-4 w-4" />
          Cerrar sesión
        </button>
      </div>
    </aside>
  );
}
