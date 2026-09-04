import api from './api'
import type { Usuario } from '../types/auth'

export interface Notificacao {
  id: number
  tipo: string
  mensagem: string
  lida: boolean
  referenciaId: number | null
  dataHora: string
  origemUsuario?: Usuario | null
}

export async function listarNotificacoes(): Promise<Notificacao[]> {
  const { data } = await api.get<Notificacao[]>('/api/notificacoes')
  return data
}

export async function contarNaoLidas(): Promise<number> {
  const { data } = await api.get<{ total: number }>('/api/notificacoes/nao-lidas')
  return data.total
}

export async function marcarNotificacaoLida(id: number): Promise<Notificacao> {
  const { data } = await api.put<Notificacao>(`/api/notificacoes/${id}/lida`)
  return data
}

export async function marcarTodasLidas(): Promise<void> {
  await api.put('/api/notificacoes/lidas')
}
