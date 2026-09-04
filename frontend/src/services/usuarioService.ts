import api from './api'
import type { AuthResponse, Usuario } from '../types/auth'

export async function atualizarPerfil(payload: {
  nome: string
  email: string
}): Promise<AuthResponse> {
  const { data } = await api.put<AuthResponse>('/api/usuarios/me', payload)
  return data
}

export async function alterarSenha(payload: {
  senhaAtual: string
  novaSenha: string
  confirmarNovaSenha: string
}): Promise<void> {
  await api.put('/api/usuarios/me/senha', payload)
}

export async function atualizarFoto(file: File): Promise<Usuario> {
  const form = new FormData()
  form.append('foto', file)
  const { data } = await api.post<Usuario>('/api/usuarios/me/foto', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  return data
}
