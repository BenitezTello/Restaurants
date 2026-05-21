'use client';

import { Users, UserPlus, Shield } from 'lucide-react';
import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { authService } from '@/services/authService';
import toast from 'react-hot-toast';

const newUserSchema = z.object({
  fullName: z.string().min(2, 'Nombre requerido'),
  email: z.string().email('Email inválido'),
  password: z.string().min(8, 'Mínimo 8 caracteres'),
  role: z.enum(['ADMIN', 'RESTAURANTE_OWNER', 'CLIENTE', 'SYSTEM_INTEGRATION']),
});
type NewUserForm = z.infer<typeof newUserSchema>;

const ROLE_LABELS: Record<string, string> = {
  ADMIN: 'Administrador',
  RESTAURANTE_OWNER: 'Dueño de Restaurante',
  CLIENTE: 'Cliente',
  SYSTEM_INTEGRATION: 'Integración de Sistema',
};

const ROLE_COLORS: Record<string, string> = {
  ADMIN: 'bg-red-100 text-red-700',
  RESTAURANTE_OWNER: 'bg-orange-100 text-orange-700',
  CLIENTE: 'bg-blue-100 text-blue-700',
  SYSTEM_INTEGRATION: 'bg-purple-100 text-purple-700',
};

export default function UsersPage() {
  const [showForm, setShowForm] = useState(false);
  const [created, setCreated] = useState<{ email: string; role: string }[]>([]);

  const { register, handleSubmit, reset, formState: { errors } } = useForm<NewUserForm>({
    resolver: zodResolver(newUserSchema),
    defaultValues: { role: 'RESTAURANTE_OWNER' },
  });

  const mutation = useMutation({
    mutationFn: (data: NewUserForm) => authService.register(data),
    onSuccess: (data) => {
      toast.success(`Usuario ${data.email} creado`);
      setCreated(prev => [...prev, { email: data.email, role: data.role }]);
      reset();
      setShowForm(false);
    },
    onError: () => toast.error('Error al crear el usuario (puede que el email ya exista)'),
  });

  return (
    <div>
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="font-display text-2xl font-bold text-gray-900">Usuarios</h1>
          <p className="text-gray-600 mt-1">Gestión de usuarios del sistema</p>
        </div>
        <button
          onClick={() => setShowForm(!showForm)}
          className="inline-flex items-center gap-2 rounded-xl bg-orange-500 hover:bg-orange-600 px-4 py-2.5 text-sm font-semibold text-white transition-colors"
        >
          <UserPlus className="h-4 w-4" />
          Nuevo usuario
        </button>
      </div>

      {/* Formulario */}
      {showForm && (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 mb-6">
          <h2 className="font-display text-lg font-semibold text-gray-900 mb-4">Crear nuevo usuario</h2>
          <form onSubmit={handleSubmit((d) => mutation.mutate(d))} className="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nombre completo</label>
              <input {...register('fullName')} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500" />
              {errors.fullName && <p className="text-xs text-red-500 mt-1">{errors.fullName.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
              <input {...register('email')} type="email" className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500" />
              {errors.email && <p className="text-xs text-red-500 mt-1">{errors.email.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
              <input {...register('password')} type="password" className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500" />
              {errors.password && <p className="text-xs text-red-500 mt-1">{errors.password.message}</p>}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Rol</label>
              <select {...register('role')} className="w-full border border-gray-200 rounded-xl px-3 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-orange-500">
                {Object.entries(ROLE_LABELS).map(([value, label]) => (
                  <option key={value} value={value}>{label}</option>
                ))}
              </select>
            </div>
            <div className="sm:col-span-2 flex gap-3">
              <button type="submit" disabled={mutation.isPending} className="px-6 py-2.5 bg-orange-500 hover:bg-orange-600 disabled:opacity-60 text-white text-sm font-semibold rounded-xl transition-colors">
                {mutation.isPending ? 'Creando...' : 'Crear usuario'}
              </button>
              <button type="button" onClick={() => setShowForm(false)} className="px-6 py-2.5 border border-gray-200 text-sm font-medium rounded-xl hover:bg-gray-50 transition-colors">
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Roles del sistema */}
      <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 mb-6">
        <h2 className="font-display text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
          <Shield className="h-5 w-5 text-orange-500" /> Roles del Sistema
        </h2>
        <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
          {Object.entries(ROLE_LABELS).map(([role, label]) => (
            <div key={role} className="flex items-center gap-3 p-3 rounded-xl bg-gray-50">
              <span className={`inline-flex px-2.5 py-1 rounded-full text-xs font-medium ${ROLE_COLORS[role]}`}>{label}</span>
              <span className="text-xs text-gray-500 font-mono">{role}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Usuarios creados en esta sesión */}
      {created.length > 0 && (
        <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6">
          <h2 className="font-display text-lg font-semibold text-gray-900 mb-4">Usuarios creados en esta sesión</h2>
          <div className="space-y-2">
            {created.map((u, i) => (
              <div key={i} className="flex items-center gap-3 py-2 border-b border-gray-50 last:border-0">
                <div className="flex h-8 w-8 items-center justify-center rounded-full bg-orange-100 text-orange-600 text-sm font-semibold">
                  {u.email.charAt(0).toUpperCase()}
                </div>
                <p className="text-sm text-gray-900">{u.email}</p>
                <span className={`ml-auto inline-flex px-2 py-0.5 rounded-full text-xs font-medium ${ROLE_COLORS[u.role]}`}>
                  {ROLE_LABELS[u.role]}
                </span>
              </div>
            ))}
          </div>
          <p className="text-xs text-gray-400 mt-3">Para ver todos los usuarios usa la base de datos directamente o la API.</p>
        </div>
      )}

      {created.length === 0 && !showForm && (
        <div className="text-center py-16 text-gray-400">
          <Users className="h-14 w-14 mx-auto mb-4 opacity-30" />
          <p>Crea usuarios para gestionar el acceso al sistema</p>
        </div>
      )}
    </div>
  );
}
