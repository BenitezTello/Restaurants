import { api, extractData } from './api';
import type { AuthUser, LoginDto, RegisterDto } from '@/types/auth';

export const authService = {
  async login(data: LoginDto) {
    return extractData<AuthUser>(await api.post('/v1/auth/login', data));
  },

  async register(data: RegisterDto) {
    return extractData<AuthUser>(await api.post('/v1/auth/register', data));
  },
};
