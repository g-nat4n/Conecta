import { useEffect, useRef, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { UserAvatar } from '../ui/UserAvatar'
import {
  contarNaoLidas,
  listarNotificacoes,
  marcarNotificacaoLida,
  type Notificacao,
} from '../../services/notificacaoService'
import { formatRelative } from '../../utils/media'
import './NotificationDropdown.css'

export function NotificationDropdown() {
  const [open, setOpen] = useState(false)
  const [items, setItems] = useState<Notificacao[]>([])
  const [total, setTotal] = useState(0)
  const ref = useRef<HTMLDivElement>(null)
  const navigate = useNavigate()

  async function carregar() {
    const [lista, count] = await Promise.all([listarNotificacoes(), contarNaoLidas()])
    setItems(lista.slice(0, 12))
    setTotal(count)
  }

  useEffect(() => {
    void carregar().catch(() => undefined)
    const id = window.setInterval(() => {
      void carregar().catch(() => undefined)
    }, 20000)
    return () => window.clearInterval(id)
  }, [])

  useEffect(() => {
    function onDocClick(event: MouseEvent) {
      if (!ref.current?.contains(event.target as Node)) setOpen(false)
    }
    document.addEventListener('mousedown', onDocClick)
    return () => document.removeEventListener('mousedown', onDocClick)
  }, [])

  return (
    <div className="notif" ref={ref}>
      <button
        type="button"
        className="icon-btn"
        title="Notificações"
        aria-label="Notificações"
        onClick={() => {
          setOpen((v) => !v)
          void carregar()
        }}
      >
        <svg viewBox="0 0 24 24" aria-hidden>
          <path
            d="M12 22a2.2 2.2 0 0 0 2.2-2.2h-4.4A2.2 2.2 0 0 0 12 22Zm7-5.5V11a7 7 0 1 0-14 0v5.5L3 18v1h18v-1l-2-1.5Z"
            fill="currentColor"
          />
        </svg>
        {total > 0 ? <em>{total > 9 ? '9+' : total}</em> : null}
      </button>

      {open ? (
        <div className="notif-dropdown glass-panel">
          <header>
            <strong>Notificações</strong>
          </header>
          <ul>
            {items.length === 0 ? <li className="empty">Nenhuma notificação</li> : null}
            {items.map((n) => (
              <li key={n.id}>
                <button
                  type="button"
                  className={n.lida ? '' : 'unread'}
                  onClick={async () => {
                    await marcarNotificacaoLida(n.id)
                    if (n.tipo === 'NOVA_MENSAGEM' && n.referenciaId) navigate(`/chat?amigo=${n.referenciaId}`)
                    else if (n.tipo.includes('AMIZADE')) navigate('/amigos')
                    else navigate('/')
                    setOpen(false)
                    void carregar()
                  }}
                >
                  <UserAvatar
                    nome={n.origemUsuario?.nome}
                    foto={n.origemUsuario?.foto}
                    size={40}
                    className="notif-avatar"
                  />
                  <div className="notif-content">
                    <span>{n.mensagem}</span>
                    <small>{formatRelative(n.dataHora)}</small>
                  </div>
                </button>
              </li>
            ))}
          </ul>
          <footer>
            <Link to="/amigos" onClick={() => setOpen(false)}>
              Ver amigos
            </Link>
          </footer>
        </div>
      ) : null}
    </div>
  )
}
