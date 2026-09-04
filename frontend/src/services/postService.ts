import api from './api'
import type { Comentario, CurtidaResult, PageResponse, Post } from '../types/post'

export async function listarPosts(page = 0, size = 20): Promise<PageResponse<Post>> {
  const { data } = await api.get<PageResponse<Post>>('/api/posts', { params: { page, size } })
  return data
}

export async function criarPost(conteudo: string, imagem?: File | null): Promise<Post> {
  if (imagem) {
    const form = new FormData()
    form.append('conteudo', conteudo)
    form.append('imagem', imagem)
    const { data } = await api.post<Post>('/api/posts', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    return data
  }

  const { data } = await api.post<Post>('/api/posts', { conteudo })
  return data
}

export async function atualizarPost(id: number, conteudo: string): Promise<Post> {
  const { data } = await api.put<Post>(`/api/posts/${id}`, { conteudo })
  return data
}

export async function excluirPost(id: number): Promise<void> {
  await api.delete(`/api/posts/${id}`)
}

export async function alternarCurtida(id: number): Promise<CurtidaResult> {
  const { data } = await api.post<CurtidaResult>(`/api/posts/${id}/curtidas`)
  return data
}

export async function listarComentarios(postId: number): Promise<Comentario[]> {
  const { data } = await api.get<Comentario[]>(`/api/posts/${postId}/comentarios`)
  return data
}

export async function criarComentario(postId: number, conteudo: string): Promise<Comentario> {
  const { data } = await api.post<Comentario>(`/api/posts/${postId}/comentarios`, { conteudo })
  return data
}

export async function atualizarComentario(id: number, conteudo: string): Promise<Comentario> {
  const { data } = await api.put<Comentario>(`/api/comentarios/${id}`, { conteudo })
  return data
}

export async function excluirComentario(id: number): Promise<void> {
  await api.delete(`/api/comentarios/${id}`)
}
