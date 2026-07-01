'use client';

import { useEffect, useState } from 'react';
import { useRouter, usePathname } from 'next/navigation';
import Link from 'next/link';
import { ArrowLeft, User, Calendar, Clock, UtensilsCrossed } from 'lucide-react';
import { useAuthStore } from '@/store/authStore';
import { AuthNav } from '@/components/ui/AuthNav';
import { cn } from '@/utils/cn';

export default function ProfileLayout({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const user = useAuthStore((s) => s.user);
  const hasHydrated = useAuthStore((s) => s.hasHydrated);
  const router = useRouter();
  const pathname = usePathname();

  // Protegemos la ruta para que solo usuarios autenticados entren
  useEffect(() => {
    if (!hasHydrated) return;
    if (!isAuthenticated) {
      router.replace('/login');
    }
  }, [hasHydrated, isAuthenticated, router]);

  if (!hasHydrated) {
    return (
      <div className="flex h-screen items-center justify-center bg-gray-50">
        <div className="h-8 w-8 animate-spin rounded-full border-4 border-orange-500 border-t-transparent" />
      </div>
    );
  }

  if (!isAuthenticated) return null;

  const tabs = [
    { href: '/profile', icon: User, label: 'Mi Perfil' },
    { href: '/profile/reservations', icon: Calendar, label: 'Mis Reservas' },
    { href: '/profile/history', icon: Clock, label: 'Mi Historial' },
  ];

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-[#0A0908] flex flex-col">
      {/* Header Estilo Público */}
      <div className="bg-gradient-to-r from-orange-600 to-orange-500 dark:from-[#3E1408] dark:to-[#7C2D12] py-4 shadow-sm">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 flex items-center justify-between gap-3 flex-wrap">
          <button onClick={() => router.push('/restaurants')} className="inline-flex items-center gap-1.5 rounded-xl px-3.5 py-2 text-sm font-medium shadow-sm bg-white text-orange-600 hover:bg-orange-50 border border-white/50 dark:bg-white/10 dark:text-white dark:border-white/20 dark:backdrop-blur-md dark:hover:bg-white/20 transition-all cursor-pointer">
            <ArrowLeft className="h-4 w-4" />
            Volver
          </button>
          <AuthNav />
        </div>
      </div>

      {/* Tabs de navegación para el cliente */}
      <div className="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 sticky top-0 z-10 shadow-sm">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-6 overflow-x-auto hide-scrollbar">
            {tabs.map(tab => {
              const isActive = pathname === tab.href;
              return (
                <Link
                  key={tab.href}
                  href={tab.href}
                  className={cn(
                    "flex items-center gap-2 py-4 text-sm font-medium border-b-2 whitespace-nowrap transition-colors",
                    isActive
                      ? "border-orange-500 text-orange-600 dark:text-orange-400"
                      : "border-transparent text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-200 hover:border-gray-300"
                  )}
                >
                  <tab.icon className={cn("h-4 w-4", isActive ? "text-orange-500 dark:text-orange-400" : "text-gray-400")} />
                  {tab.label}
                </Link>
              );
            })}
          </div>
        </div>
      </div>

      {/* Contenido */}
      <main className="flex-1 w-full mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  );
}
