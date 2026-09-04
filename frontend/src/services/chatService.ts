import api from './api'
import type { Usuario } from '../types/auth'

export interface Mensagem {
  id: number
  remetente: Usuario
  destinatario: Usuario
  conteudo: string
  dataHora: string
  status: 'ENVIADA' | 'ENTREGUE' | 'LIDA' | 'EDITADA' | 'EXCLUIDA'
  dataAtualizacao: string
}

export interface Conversa {
  usuario: Usuario
  ultimaMensagem: string
  horario: string
  naoLidas: number
}

export async function listarConversas(): Promise<Conversa[]> {
  const { data } = await api.get<Conversa[]>('/api/chat/conversas')
  return data
}

export async function listarMensagens(amigoId: number): Promise<Mensagem[]> {
  const { data } = await api.get<Mensagem[]>(`/api/chat/${amigoId}`)
  return data
}

export async function enviarMensagem(amigoId: number, conteudo: string): Promise<Mensagem> {
  const { data } = await api.post<Mensagem>(`/api/chat/${amigoId}`, { conteudo })
  return data
}

export async function editarMensagem(id: number, conteudo: string): Promise<Mensagem> {
  const { data } = await api.put<Mensagem>(`/api/chat/mensagens/${id}`, { conteudo })
  return data
}

export async function excluirMensagem(id: number): Promise<void> {
  await api.delete(`/api/chat/mensagens/${id}`)
}
