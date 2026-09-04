import { useCallback, useEffect, useMemo, useRef, useState, type FormEvent } from 'react'
import { useSearchParams } from 'react-router-dom'
import { Button } from '../components/ui/Button'
import { useAuth } from '../contexts/AuthContext'
import { listarAmigos } from '../services/amigoService'
import {
  editarMensagem,
  enviarMensagem,
  excluirMensagem,
  listarConversas,
  listarMensagens,
  type Conversa,
  type Mensagem,
} from '../services/chatService'
import type { Usuario } from '../types/auth'
import { getErrorMessage } from '../utils/error'
import { formatDateTime, mediaUrl } from '../utils/media'
import './ChatPage.css'

export function ChatPage() {
  const { usuario } = useAuth()
  const [params, setParams] = useSearchParams()
  const amigoParam = params.get('amigo')

  const [conversas, setConversas] = useState<Conversa[]>([])
  const [amigos, setAmigos] = useState<Usuario[]>([])
  const [mensagens, setMensagens] = useState<Mensagem[]>([])
  const [selecionado, setSelecionado] = useState<Usuario | null>(null)
  const [texto, setTexto] = useState('')
  const [filtro, setFiltro] = useState('')
  const [erro, setErro] = useState('')
  const [carregandoMensagens, setCarregandoMensagens] = useState(false)

  const messagesRef = useRef<HTMLDivElement>(null)
  const selecionadoIdRef = useRef<number | null>(null)
  const openingRef = useRef(false)

  const carregarConversas = useCallback(async () => {
    const [listaConversas, listaAmigos] = await Promise.all([listarConversas(), listarAmigos()])
    setConversas(listaConversas)
    setAmigos(listaAmigos)
  }, [])

  useEffect(() => {
    void carregarConversas().catch((error) => setErro(getErrorMessage(error)))
  }, [carregarConversas])

  const scrollParaBaixo = useCallback((force = false) => {
    const el = messagesRef.current
    if (!el) return
    const pertoDoFim = el.scrollHeight - el.scrollTop - el.clientHeight < 120
    if (force || pertoDoFim) {
      el.scrollTop = el.scrollHeight
    }
  }, [])

  useEffect(() => {
    scrollParaBaixo(true)
  }, [mensagens, selecionado?.id, scrollParaBaixo])

  const abrirConversa = useCallback(
    async (amigo: Usuario, forceReload = false) => {
      if (openingRef.current) return
      if (!forceReload && selecionadoIdRef.current === amigo.id) {
        setSelecionado(amigo)
        return
      }

      openingRef.current = true
      selecionadoIdRef.current = amigo.id
      setSelecionado(amigo)
      setParams({ amigo: String(amigo.id) }, { replace: true })
      setCarregandoMensagens(true)
      setErro('')

      try {
        const lista = await listarMensagens(amigo.id)
        if (selecionadoIdRef.current === amigo.id) {
          setMensagens(lista)
        }
        await carregarConversas()
      } catch (error) {
        setErro(getErrorMessage(error))
      } finally {
        setCarregandoMensagens(false)
        openingRef.current = false
      }
    },
    [carregarConversas, setParams],
  )

  useEffect(() => {
    if (!amigoParam || amigos.length === 0) return
    const amigo = amigos.find((a) => String(a.id) === amigoParam)
    if (!amigo) return
    if (selecionadoIdRef.current === amigo.id) return
    void abrirConversa(amigo)
  }, [amigoParam, amigos, abrirConversa])

  async function enviar(event: FormEvent) {
    event.preventDefault()
    if (!selecionado || !texto.trim()) return
    const conteudo = texto.trim()
    setTexto('')
    try {
      const msg = await enviarMensagem(selecionado.id, conteudo)
      setMensagens((prev) => {
        if (prev.some((m) => m.id === msg.id)) return prev
        return [...prev, msg]
      })
      void carregarConversas()
    } catch (error) {
      setTexto(conteudo)
      setErro(getErrorMessage(error))
    }
  }

  const listaLateral = useMemo(() => {
    const mapa = new Map<number, Conversa>()
    conversas.forEach((c) => mapa.set(c.usuario.id, c))
    amigos.forEach((amigo) => {
      if (!mapa.has(amigo.id)) {
        mapa.set(amigo.id, {
          usuario: amigo,
          ultimaMensagem: 'Nenhuma mensagem ainda',
          horario: '',
          naoLidas: 0,
        })
      }
    })
    return Array.from(mapa.values()).filter((c) =>
      c.usuario.nome.toLowerCase().includes(filtro.toLowerCase()),
    )
  }, [conversas, amigos, filtro])

  if (!usuario) return null

  return (
    <div className="chat-page glass-panel">
      <aside className="chat-sidebar">
        <div className="chat-sidebar__head">
          <h1>Mensagens</h1>
          <input
            value={filtro}
            onChange={(e) => setFiltro(e.target.value)}
            placeholder="Pesquisar conversas"
          />
        </div>
        <ul className="chat-list">
          {listaLateral.map((c) => {
            const foto = mediaUrl(c.usuario.foto)
            return (
              <li key={c.usuario.id}>
                <button
                  type="button"
                  className={selecionado?.id === c.usuario.id ? 'active' : ''}
                  onClick={() => void abrirConversa(c.usuario, true)}
                >
                  {foto ? <img src={foto} alt="" /> : <span>{c.usuario.nome.charAt(0)}</span>}
                  <div>
                    <strong>{c.usuario.nome}</strong>
                    <small>{c.ultimaMensagem}</small>
                  </div>
                  <div className="chat-list__meta">
                    {c.horario ? <time>{formatDateTime(c.horario)}</time> : null}
                    {c.naoLidas > 0 ? <em>{c.naoLidas}</em> : null}
                  </div>
                </button>
              </li>
            )
          })}
        </ul>
      </aside>

      <section className="chat-window">
        {!selecionado ? (
          <div className="chat-empty">Selecione uma conversa para começar.</div>
        ) : (
          <>
            <header className="chat-window__head">
              {mediaUrl(selecionado.foto) ? (
                <img src={mediaUrl(selecionado.foto)} alt="" />
              ) : (
                <span>{selecionado.nome.charAt(0)}</span>
              )}
              <div>
                <h2>{selecionado.nome}</h2>
                <small>Conversa</small>
              </div>
            </header>

            <div className="chat-messages" ref={messagesRef}>
              <div className="chat-messages__inner">
                {carregandoMensagens && mensagens.length === 0 ? (
                  <p className="chat-loading">Carregando mensagens...</p>
                ) : null}
                {mensagens
                  .filter((m) => m.status !== 'EXCLUIDA')
                  .map((m) => {
                    const mine = m.remetente.id === usuario.id
                    return (
                      <div key={m.id} className={`bubble ${mine ? 'mine' : ''}`}>
                        <p>{m.conteudo}</p>
                        <small>
                          {formatDateTime(m.dataHora)}
                          {m.status === 'EDITADA' ? ' · editada' : ''}
                        </small>
                        {mine ? (
                          <div className="bubble__actions">
                            <button
                              type="button"
                              onClick={async () => {
                                const novo = window.prompt('Editar mensagem', m.conteudo)
                                if (!novo?.trim()) return
                                const atualizada = await editarMensagem(m.id, novo.trim())
                                setMensagens((prev) => prev.map((x) => (x.id === m.id ? atualizada : x)))
                              }}
                            >
                              Editar
                            </button>
                            <button
                              type="button"
                              onClick={async () => {
                                await excluirMensagem(m.id)
                                setMensagens((prev) => prev.filter((x) => x.id !== m.id))
                                void carregarConversas()
                              }}
                            >
                              Excluir
                            </button>
                          </div>
                        ) : null}
                      </div>
                    )
                  })}
              </div>
            </div>

            <form className="chat-composer" onSubmit={enviar}>
              <input
                value={texto}
                onChange={(e) => setTexto(e.target.value)}
                placeholder="Digite uma mensagem"
                maxLength={4000}
              />
              <Button type="submit">Enviar</Button>
            </form>
          </>
        )}
        {erro ? <div className="chat-alert">{erro}</div> : null}
      </section>
    </div>
  )
}
