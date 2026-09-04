export interface Usuario {
  id: number
  nome: string
  email: string
  foto: string | null
  dataCriacao: string
}

export interface AuthResponse {
  token: string
  tipo: string
  usuario: Usuario
}

export interface LoginPayload {
  email: string
  senha: string
}

export interface RegistroPayload {
  nome: string
  email: string
  senha: string
  confirmarSenha: string
}

export interface ApiError {
  status: number
  erro: string
  mensagem: string
  caminho?: string
  timestamp?: string
}
