import Link from 'next/link';
import { MapPin, Star, Calendar, UtensilsCrossed, ChevronRight } from 'lucide-react';

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-orange-50 to-white">
      {/* Hero */}
      <header className="relative overflow-hidden bg-gradient-to-r from-orange-600 to-orange-500">
        <div className="absolute inset-0 bg-black/20" />
        <div className="relative mx-auto max-w-7xl px-4 py-24 sm:px-6 lg:px-8">
          <div className="text-center">
            <div className="flex items-center justify-center gap-2 text-orange-200 mb-4">
              <MapPin className="h-5 w-5" />
              <span className="text-sm font-medium">Tingo María, Huánuco, Perú</span>
            </div>
            <h1 className="font-display text-4xl font-bold tracking-tight text-white sm:text-6xl">
              Descubre los mejores
              <br />
              <span className="text-orange-200">restaurantes</span>
            </h1>
            <p className="mt-6 text-lg text-orange-100 max-w-2xl mx-auto">
              Reserva mesas, explora menús y descubre restaurantes cercanos a los eventos más importantes de Tingo María.
            </p>
            <div className="mt-10 flex gap-4 justify-center flex-wrap">
              <Link
                href="/restaurants"
                className="inline-flex items-center gap-2 rounded-xl bg-white px-8 py-4 text-base font-semibold text-orange-600 shadow-lg hover:bg-orange-50 transition-colors"
              >
                <UtensilsCrossed className="h-5 w-5" />
                Ver Restaurantes
              </Link>
              <Link
                href="/dashboard"
                className="inline-flex items-center gap-2 rounded-xl border-2 border-white px-8 py-4 text-base font-semibold text-white hover:bg-white/10 transition-colors"
              >
                Panel de Gestión
                <ChevronRight className="h-5 w-5" />
              </Link>
              <Link
                href="/reservations"
                className="inline-flex items-center gap-2 rounded-xl border-2 border-orange-300 px-8 py-4 text-base font-semibold text-orange-100 hover:bg-white/10 transition-colors"
              >
                Consultar Reserva
              </Link>
            </div>
          </div>
        </div>
      </header>

      {/* Features */}
      <section className="mx-auto max-w-7xl px-4 py-20 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="font-display text-3xl font-bold text-gray-900">
            Todo lo que necesitas
          </h2>
          <p className="mt-4 text-gray-600">Gestión completa del ecosistema gastronómico</p>
        </div>
        <div className="grid grid-cols-1 gap-8 sm:grid-cols-2 lg:grid-cols-3">
          {features.map((feature) => (
            <div
              key={feature.title}
              className="group rounded-2xl border border-gray-100 bg-white p-8 shadow-sm hover:shadow-md transition-shadow"
            >
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-orange-100 text-orange-600 mb-6">
                <feature.icon className="h-6 w-6" />
              </div>
              <h3 className="font-display text-lg font-semibold text-gray-900">{feature.title}</h3>
              <p className="mt-2 text-gray-600 text-sm leading-relaxed">{feature.description}</p>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className="bg-orange-600 py-16">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="font-display text-3xl font-bold text-white">
            ¿Tienes un restaurante?
          </h2>
          <p className="mt-4 text-orange-100">
            Únete a la plataforma y llega a miles de turistas y locales.
          </p>
          <Link
            href="/register"
            className="mt-8 inline-flex items-center gap-2 rounded-xl bg-white px-8 py-4 font-semibold text-orange-600 hover:bg-orange-50 transition-colors"
          >
            Registrar mi restaurante
            <ChevronRight className="h-5 w-5" />
          </Link>
        </div>
      </section>
    </div>
  );
}

const features = [
  {
    icon: UtensilsCrossed,
    title: 'Catálogo de Restaurantes',
    description: 'Explora restaurantes verificados con menús completos, fotos y reseñas reales de clientes.',
  },
  {
    icon: Calendar,
    title: 'Reservas en Tiempo Real',
    description: 'Reserva tu mesa al instante con confirmación inmediata y código de seguimiento.',
  },
  {
    icon: MapPin,
    title: 'Búsqueda Geoespacial',
    description: 'Encuentra restaurantes cercanos a eventos culturales, hoteles y atracciones turísticas.',
  },
  {
    icon: Star,
    title: 'Calificaciones Verificadas',
    description: 'Lee reseñas de clientes que visitaron el restaurante y filtra por calificación.',
  },
  {
    icon: Calendar,
    title: 'Eventos Especiales',
    description: 'Restaurantes que aceptan grupos para fiestas, aniversarios y eventos masivos.',
  },
  {
    icon: MapPin,
    title: 'Integración con el Ecosistema',
    description: 'Conectado con el sistema de hoteles, eventos y turismo de Tingo María.',
  },
];
