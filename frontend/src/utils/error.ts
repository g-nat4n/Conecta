import axios from 'axios'
import type { ApiError } from '../types/auth'

export function getErrorMessage(error: unknown, fallback = 'Algo deu errado. Tente novamente.'): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as ApiError | undefined
    if (data?.mensagem) return data.mensagem
    if (error.response?.status === 401) return 'Email ou senha inválidos'
    if (error.response?.status === 409) return 'Este email já está cadastrado'
    if (!error.response) return 'Não foi possível conectar ao servidor'
  }
  if (error instanceof Error && error.message) return error.message
  return fallback
}
