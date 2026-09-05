import { useEffect, useState, type FormEvent } from 'react'
import { useAuth } from '../../contexts/AuthContext'
import { useToast } from '../../contexts/ToastContext'
import {
  alternarCurtida,
  atualizarComentario,
  atualizarPost,
  criarComentario,
  excluirComentario,
  excluirPost,
  listarComentarios,
} from '../../services/postService'
import type { TipoDenuncia } from '../../services/denunciaService'
import type { Comentario, Post } from '../../types/post'
import { getErrorMessage } from '../../utils/error'
import { formatRelative, mediaUrl } from '../../utils/media'
import { Button } from '../ui/Button'
import { UserAvatar } from '../ui/UserAvatar'
import { DenunciarModal } from './DenunciarModal'
import './PostCard.css'

interface PostCardProps {
  post: Post
  onChanged: () => void
}

export function PostCard({ post, onChanged }: PostCardProps) {
  const { usuario } = useAuth()
  const { pushToast } = useToast()
  const [curtido, setCurtido] = useState(post.curtidoPorMim)
  const [totalCurtidas, setTotalCurtidas] = useState(post.totalCurtidas)
  const [mostrarComentarios, setMostrarComentarios] = useState(false)
  const [comentarios, setComentarios] = useState<Comentario[]>([])
  const [novoComentario, setNovoComentario] = useState('')
  const [editando, setEditando] = useState(false)
  const [conteudoEdit, setConteudoEdit] = useState(post.conteudo)
  const [erro, setErro] = useState('')
  const [loading, setLoading] = useState(false)
  const [denuncia, setDenuncia] = useState<{ tipo: TipoDenuncia; referenciaId: number } | null>(null)

  const autorNome = post.autor?.nome?.trim() || 'Usuário'
  const isDono = usuario?.id === post.autor?.id
  const imagemPost = mediaUrl(post.imagem)

  useEffect(() => {
    setCurtido(post.curtidoPorMim)
    setTotalCurtidas(post.totalCurtidas)
    setConteudoEdit(post.conteudo)
  }, [post])

  async function handleCurtir() {
    try {
      const result = await alternarCurtida(post.id)
      setCurtido(result.curtidoPorMim)
      setTotalCurtidas(result.totalCurtidas)
    } catch (error) {
      setErro(getErrorMessage(error))
    }
  }

  async function carregarComentarios() {
    const lista = await listarComentarios(post.id)
    setComentarios(lista)
  }

  async function toggleComentarios() {
    const next = !mostrarComentarios
    setMostrarComentarios(next)
    if (next) {
      try {
        await carregarComentarios()
      } catch (error) {
        setErro(getErrorMessage(error))
      }
    }
  }

  async function enviarComentario(event: FormEvent) {
    event.preventDefault()
    if (!novoComentario.trim()) return
    setLoading(true)
    try {
      await criarComentario(post.id, novoComentario.trim())
      setNovoComentario('')
      await carregarComentarios()
      onChanged()
    } catch (error) {
      setErro(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  async function salvarEdicao() {
    setLoading(true)
    try {
      await atualizarPost(post.id, conteudoEdit.trim())
      setEditando(false)
      onChanged()
    } catch (error) {
      setErro(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  async function removerPost() {
    if (!window.confirm('Excluir esta publicação?')) return
    try {
      await excluirPost(post.id)
      onChanged()
    } catch (error) {
      setErro(getErrorMessage(error))
    }
  }

  async function removerComentario(id: number) {
    try {
      await excluirComentario(id)
      await carregarComentarios()
      onChanged()
    } catch (error) {
      setErro(getErrorMessage(error))
    }
  }

  async function editarComentario(comentario: Comentario) {
    const valor = window.prompt('Editar comentário', comentario.conteudo)
    if (valor == null || !valor.trim()) return
    try {
      await atualizarComentario(comentario.id, valor.trim())
      await carregarComentarios()
    } catch (error) {
      setErro(getErrorMessage(error))
    }
  }

  return (
    <article className="post-card glass-panel">
      <header className="post-card__header">
        <UserAvatar nome={autorNome} foto={post.autor?.foto} size={42} className="post-card__avatar" />
        <div className="post-card__meta">
          <h3>{autorNome}</h3>
          <time>{formatRelative(post.dataCriacao)}</time>
        </div>
        {isDono ? (
          <div className="post-card__owner">
            <button type="button" onClick={() => setEditando((v) => !v)}>
              Editar
            </button>
            <button type="button" onClick={() => void removerPost()}>
              Excluir
            </button>
          </div>
        ) : (
          <div className="post-card__owner">
            <button
              type="button"
              className="post-card__denunciar"
              onClick={() => setDenuncia({ tipo: 'POST', referenciaId: post.id })}
            >
              Denunciar
            </button>
          </div>
        )}
      </header>

      {editando ? (
        <div className="post-card__edit">
          <textarea value={conteudoEdit} onChange={(e) => setConteudoEdit(e.target.value)} rows={3} />
          <Button type="button" loading={loading} onClick={() => void salvarEdicao()}>
            Salvar
          </Button>
        </div>
      ) : (
        <p className="post-card__content">{post.conteudo}</p>
      )}

      {imagemPost ? <img src={imagemPost} alt="" className="post-card__image" /> : null}

      <div className="post-card__stats">
        <span>{totalCurtidas} curtidas</span>
        <span>{post.totalComentarios} comentários</span>
      </div>

      <footer className="post-card__footer">
        <button type="button" className={curtido ? 'active' : ''} onClick={() => void handleCurtir()}>
          {curtido ? 'Descurtir' : 'Curtir'}
        </button>
        <button type="button" onClick={() => void toggleComentarios()}>
          Comentar
        </button>
        {!isDono ? (
          <button
            type="button"
            className="post-card__denunciar-footer"
            onClick={() => setDenuncia({ tipo: 'POST', referenciaId: post.id })}
          >
            Denunciar
          </button>
        ) : (
          <button type="button" disabled title="Em breve">
            Compartilhar
          </button>
        )}
      </footer>

      {mostrarComentarios ? (
        <div className="post-card__comments">
          <form onSubmit={enviarComentario} className="post-card__comment-form">
            <input
              value={novoComentario}
              onChange={(e) => setNovoComentario(e.target.value)}
              placeholder="Escreva um comentário..."
              maxLength={1000}
            />
            <Button type="submit" loading={loading}>
              Enviar
            </Button>
          </form>
          <ul>
            {comentarios.map((comentario) => (
              <li key={comentario.id}>
                <strong>{comentario.autor.nome}</strong>
                <span>{comentario.conteudo}</span>
                <small>{formatRelative(comentario.dataCriacao)}</small>
                {usuario?.id === comentario.autor.id ? (
                  <div className="post-card__comment-actions">
                    <button type="button" onClick={() => void editarComentario(comentario)}>
                      Editar
                    </button>
                    <button type="button" onClick={() => void removerComentario(comentario.id)}>
                      Excluir
                    </button>
                  </div>
                ) : (
                  <div className="post-card__comment-actions">
                    <button
                      type="button"
                      onClick={() =>
                        setDenuncia({ tipo: 'COMENTARIO', referenciaId: comentario.id })
                      }
                    >
                      Denunciar
                    </button>
                  </div>
                )}
              </li>
            ))}
          </ul>
        </div>
      ) : null}

      {erro ? <p className="post-card__error">{erro}</p> : null}

      <DenunciarModal
        open={denuncia != null}
        tipo={denuncia?.tipo ?? 'POST'}
        referenciaId={denuncia?.referenciaId ?? 0}
        onClose={() => setDenuncia(null)}
        onSuccess={() =>
          pushToast({
            mensagem: 'Denúncia enviada. Obrigado por ajudar a manter o Conecta seguro.',
            tipo: 'DENUNCIA',
          })
        }
      />
    </article>
  )
}
