import api from './api'
import type { AuthResponse, LoginPayload, RegistroPayload, Usuario } from '../types/auth'

export async function login(payload: LoginPayload): Promise<AuthResponse> {
  const { data } = await api.post<AuthResponse>('/api/auth/login', payload)
  return data
}

export async function registrar(payload: RegistroPayload): Promise<Usuario> {
  const { data } = await api.post<Usuario>('/api/auth/registro', payload)
  return data
}

export async function buscarMe(): Promise<Usuario> {
  const { data } = await api.get<Usuario>('/api/usuarios/me')
  return data
}
