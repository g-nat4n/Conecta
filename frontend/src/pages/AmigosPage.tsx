import { useCallback, useEffect, useMemo, useState } from 'react'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { UserAvatar } from '../components/ui/UserAvatar'
import {
  aceitarSolicitacao,
  enviarSolicitacao,
  listarAmigos,
  listarSolicitacoes,
  listarSolicitacoesEnviadas,
  pesquisarUsuarios,
  recusarSolicitacao,
  removerAmigo,
  type SolicitacaoAmizade,
} from '../services/amigoService'
import type { Usuario } from '../types/auth'
import { getErrorMessage } from '../utils/error'
import './AmigosPage.css'

export function AmigosPage() {
  const [amigos, setAmigos] = useState<Usuario[]>([])
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoAmizade[]>([])
  const [enviadas, setEnviadas] = useState<SolicitacaoAmizade[]>([])
  const [resultados, setResultados] = useState<Usuario[]>([])
  const [busca, setBusca] = useState('')
  const [erro, setErro] = useState('')
  const [loading, setLoading] = useState(true)
  const [enviandoId, setEnviandoId] = useState<number | null>(null)

  const amigoIds = useMemo(() => new Set(amigos.map((a) => a.id)), [amigos])
  const enviadosIds = useMemo(() => new Set(enviadas.map((s) => s.destinatario.id)), [enviadas])

  const carregar = useCallback(async () => {
    try {
      setErro('')
      const [listaAmigos, listaSolicitacoes, listaEnviadas] = await Promise.all([
        listarAmigos(),
        listarSolicitacoes(),
        listarSolicitacoesEnviadas(),
      ])
      setAmigos(listaAmigos)
      setSolicitacoes(listaSolicitacoes)
      setEnviadas(listaEnviadas)
    } catch (error) {
      setErro(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void carregar()
  }, [carregar])

  async function pesquisar() {
    if (!busca.trim()) {
      setResultados([])
      return
    }
    try {
      setResultados(await pesquisarUsuarios(busca.trim()))
    } catch (error) {
      setErro(getErrorMessage(error))
    }
  }

  function statusBotao(user: Usuario) {
    if (amigoIds.has(user.id)) return 'amigo'
    if (enviadosIds.has(user.id)) return 'enviado'
    return 'adicionar'
  }

  return (
    <div className="amigos-page">
      <section className="amigos-panel glass-panel">
        <h1>Amigos</h1>
        <div className="amigos-search">
          <Input
            label="Pesquisar usuários"
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
            placeholder="Nome ou email"
          />
          <Button type="button" onClick={() => void pesquisar()}>
            Buscar
          </Button>
        </div>

        {erro ? <div className="amigos-alert">{erro}</div> : null}

        {resultados.length > 0 ? (
          <div className="amigos-block">
            <h2>Resultados</h2>
            <ul>
              {resultados.map((user) => {
                const status = statusBotao(user)
                return (
                  <li key={user.id}>
                    <UserRow user={user} />
                    {status === 'amigo' ? (
                      <span className="amigos-status">Já são amigos</span>
                    ) : status === 'enviado' ? (
                      <span className="amigos-status enviada">Pedido enviado</span>
                    ) : (
                      <Button
                        type="button"
                        loading={enviandoId === user.id}
                        onClick={async () => {
                          setEnviandoId(user.id)
                          try {
                            await enviarSolicitacao(user.id)
                            setErro('')
                            await carregar()
                          } catch (error) {
                            setErro(getErrorMessage(error))
                          } finally {
                            setEnviandoId(null)
                          }
                        }}
                      >
                        Adicionar
                      </Button>
                    )}
                  </li>
                )
              })}
            </ul>
          </div>
        ) : null}

        <div className="amigos-block">
          <h2>Solicitações ({solicitacoes.length})</h2>
          {solicitacoes.length === 0 ? <p className="amigos-empty">Nenhuma solicitação pendente.</p> : null}
          <ul>
            {solicitacoes.map((s) => (
              <li key={s.id}>
                <UserRow user={s.remetente} />
                <div className="amigos-actions">
                  <Button
                    type="button"
                    onClick={async () => {
                      await aceitarSolicitacao(s.id)
                      await carregar()
                    }}
                  >
                    Aceitar
                  </Button>
                  <Button
                    type="button"
                    variant="secondary"
                    onClick={async () => {
                      await recusarSolicitacao(s.id)
                      await carregar()
                    }}
                  >
                    Recusar
                  </Button>
                </div>
              </li>
            ))}
          </ul>
        </div>

        <div className="amigos-block">
          <h2>Pedidos enviados ({enviadas.length})</h2>
          {enviadas.length === 0 ? <p className="amigos-empty">Nenhum pedido enviado pendente.</p> : null}
          <ul>
            {enviadas.map((s) => (
              <li key={s.id}>
                <UserRow user={s.destinatario} />
                <span className="amigos-status enviada">Pedido enviado</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="amigos-block">
          <h2>Minha lista ({loading ? '...' : amigos.length})</h2>
          {amigos.length === 0 && !loading ? <p className="amigos-empty">Você ainda não tem amigos.</p> : null}
          <ul>
            {amigos.map((amigo) => (
              <li key={amigo.id}>
                <UserRow user={amigo} />
                <Button
                  type="button"
                  variant="secondary"
                  onClick={async () => {
                    await removerAmigo(amigo.id)
                    await carregar()
                  }}
                >
                  Remover
                </Button>
              </li>
            ))}
          </ul>
        </div>
      </section>
    </div>
  )
}

function UserRow({ user }: { user: Usuario }) {
  return (
    <div className="amigos-user">
      <UserAvatar nome={user.nome} foto={user.foto} size={42} />
      <div>
        <strong>{user.nome}</strong>
        <small>{user.email}</small>
      </div>
    </div>
  )
}
