import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../../services/api'
import type { Usuario } from '../../types/auth'
import { mediaUrl } from '../../utils/media'
import './UserProfileCard.css'

interface UserProfileCardProps {
  usuario: Usuario
  refreshKey?: number
}

interface Estatisticas {
  totalAmigos: number
  totalPosts: number
}

export function UserProfileCard({ usuario, refreshKey = 0 }: UserProfileCardProps) {
  const [stats, setStats] = useState<Estatisticas>({ totalAmigos: 0, totalPosts: 0 })

  useEffect(() => {
    let ativo = true
    api
      .get<Estatisticas>('/api/usuarios/me/estatisticas')
      .then(({ data }) => {
        if (ativo) setStats(data)
      })
      .catch(() => undefined)
    return () => {
      ativo = false
    }
  }, [usuario.id, refreshKey])

  const iniciais = usuario.nome
    .split(' ')
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase()
  const foto = mediaUrl(usuario.foto)

  return (
    <aside className="user-card glass-panel">
      <div className="user-card__banner" />
      <div className="user-card__body">
        {foto ? (
          <img src={foto} alt={usuario.nome} className="user-card__avatar" />
        ) : (
          <div className="user-card__avatar user-card__avatar--fallback">{iniciais}</div>
        )}
        <h2>{usuario.nome}</h2>
        <p>{usuario.email}</p>

        <div className="user-card__stats">
          <div>
            <strong>{stats.totalAmigos}</strong>
            <span>Amigos</span>
          </div>
          <div>
            <strong>{stats.totalPosts}</strong>
            <span>Posts</span>
          </div>
        </div>

        <Link to="/perfil" className="user-card__link">
          Meu perfil
        </Link>
      </div>
    </aside>
  )
}
