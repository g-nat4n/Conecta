import { useEffect, useRef } from 'react'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { useToast } from '../contexts/ToastContext'
import { getToken } from '../utils/storage'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export function useRealtime() {
  const { token, usuario } = useAuth()
  const { pushToast } = useToast()
  const navigate = useNavigate()
  const clientRef = useRef<Client | null>(null)

  useEffect(() => {
    if (!token || !usuario) return

    const client = new Client({
      webSocketFactory: () => new SockJS(`${API_URL}/ws`) as WebSocket,
      connectHeaders: {
        Authorization: `Bearer ${getToken() || token}`,
      },
      reconnectDelay: 4000,
      onConnect: () => {
        client.subscribe('/user/queue/toast', (message) => {
          try {
            const body = JSON.parse(message.body) as {
              tipo: string
              mensagem: string
              referenciaId?: number
              origemUsuario?: { nome?: string; foto?: string | null }
            }
            pushToast({
              mensagem: body.mensagem,
              tipo: body.tipo,
              referenciaId: body.referenciaId,
              foto: body.origemUsuario?.foto,
              nome: body.origemUsuario?.nome,
              onClick: () => {
                if (body.tipo === 'NOVA_MENSAGEM' && body.referenciaId) {
                  navigate(`/chat?amigo=${body.referenciaId}`)
                } else if (body.tipo === 'SOLICITACAO_AMIZADE' || body.tipo === 'NOVA_AMIZADE') {
                  navigate('/amigos')
                } else {
                  navigate('/')
                }
              },
            })
          } catch {
            // ignore malformed payloads
          }
        })
      },
    })

    client.activate()
    clientRef.current = client

    return () => {
      void client.deactivate()
      clientRef.current = null
    }
  }, [token, usuario, pushToast, navigate])
}
