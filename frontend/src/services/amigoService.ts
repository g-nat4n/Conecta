import api from './api'
import type { Usuario } from '../types/auth'

export interface SolicitacaoAmizade {
  id: number
  remetente: Usuario
  destinatario: Usuario
  status: 'PENDENTE' | 'ACEITA' | 'RECUSADA'
  dataCriacao: string
}

export async function listarAmigos(): Promise<Usuario[]> {
  const { data } = await api.get<Usuario[]>('/api/amigos')
  return data
}

export async function pesquisarUsuarios(q: string): Promise<Usuario[]> {
  const { data } = await api.get<Usuario[]>('/api/amigos/pesquisar', { params: { q } })
  return data
}

export async function listarSolicitacoes(): Promise<SolicitacaoAmizade[]> {
  const { data } = await api.get<SolicitacaoAmizade[]>('/api/amigos/solicitacoes')
  return data
}

export async function listarSolicitacoesEnviadas(): Promise<SolicitacaoAmizade[]> {
  const { data } = await api.get<SolicitacaoAmizade[]>('/api/amigos/solicitacoes/enviadas')
  return data
}

export async function enviarSolicitacao(usuarioId: number): Promise<SolicitacaoAmizade> {
  const { data } = await api.post<SolicitacaoAmizade>(`/api/amigos/solicitacoes/${usuarioId}`)
  return data
}

export async function aceitarSolicitacao(id: number): Promise<SolicitacaoAmizade> {
  const { data } = await api.post<SolicitacaoAmizade>(`/api/amigos/solicitacoes/${id}/aceitar`)
  return data
}

export async function recusarSolicitacao(id: number): Promise<SolicitacaoAmizade> {
  const { data } = await api.post<SolicitacaoAmizade>(`/api/amigos/solicitacoes/${id}/recusar`)
  return data
}

export async function removerAmigo(amigoId: number): Promise<void> {
  await api.delete(`/api/amigos/${amigoId}`)
}
