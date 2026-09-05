import api from './api'

export type TipoDenuncia = 'POST' | 'COMENTARIO'

export type StatusDenuncia = 'PENDENTE' | 'ANALISADA' | 'DESCARTADA'

export interface Denuncia {
  id: number
  tipo: string
  referenciaId: number
  motivo: string
  status: StatusDenuncia
  dataCriacao: string
  usuarioId: number | null
  usuarioNome: string | null
}

export interface CriarDenunciaPayload {
  tipo: TipoDenuncia
  referenciaId: number
  motivo: string
}

export async function criarDenuncia(payload: CriarDenunciaPayload): Promise<Denuncia> {
  const { data } = await api.post<Denuncia>('/api/denuncias', payload)
  return data
}

export async function listarDenuncias(): Promise<Denuncia[]> {
  const { data } = await api.get<Denuncia[]>('/api/denuncias')
  return data
}
