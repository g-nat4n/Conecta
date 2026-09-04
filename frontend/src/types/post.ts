export interface Post {
  id: number
  autor: {
    id: number
    nome: string
    email: string
    foto: string | null
    dataCriacao: string
  }
  conteudo: string
  imagem: string | null
  dataCriacao: string
  dataAtualizacao: string
  totalCurtidas: number
  totalComentarios: number
  curtidoPorMim: boolean
}

export interface Comentario {
  id: number
  postId: number
  autor: {
    id: number
    nome: string
    email: string
    foto: string | null
  }
  conteudo: string
  dataCriacao: string
  dataAtualizacao: string
}

export interface CurtidaResult {
  postId: number
  totalCurtidas: number
  curtidoPorMim: boolean
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
  last: boolean
}
