import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import logo from '../../assets/logo-branca.png'
import { useAuth } from '../../contexts/AuthContext'
import { mediaUrl } from '../../utils/media'
import { NotificationDropdown } from './NotificationDropdown'
import { SearchBar } from './SearchBar'
import './Header.css'

export function Header() {
  const { usuario, logout } = useAuth()
  const navigate = useNavigate()
  const [busca, setBusca] = useState('')
  const foto = mediaUrl(usuario?.foto)

  function handleLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  const iniciais = usuario?.nome
    ?.split(' ')
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase()

  return (
    <header className="app-header">
      <div className="app-header__inner">
        <Link to="/" className="app-header__brand" aria-label="Conecta - início">
          <img src={logo} alt="Conecta" className="app-header__logo" />
        </Link>

        <div className="app-header__search">
          <SearchBar value={busca} onChange={setBusca} />
        </div>

        <div className="app-header__actions">
          <NotificationDropdown />

          <Link to="/amigos" className="icon-btn" title="Amigos" aria-label="Amigos">
            <svg viewBox="0 0 24 24" aria-hidden>
              <path
                d="M16 11a4 4 0 1 0-3.9-4.8A4 4 0 0 0 16 11Zm-8 0a3.5 3.5 0 1 0-3.4-4.2A3.5 3.5 0 0 0 8 11Zm0 2c-2.8 0-8 1.4-8 4.2V20h8.2c-.1-.4-.2-.8-.2-1.2 0-1.6.7-3 1.8-4.1A13 13 0 0 0 8 13Zm8 0c-.6 0-1.2 0-1.8.1A5.2 5.2 0 0 1 18.8 18c0 .4 0 .8-.1 1.2H24v-.8C24 14.4 18.8 13 16 13Z"
                fill="currentColor"
              />
            </svg>
          </Link>

          <Link to="/chat" className="icon-btn" title="Chat" aria-label="Chat">
            <svg viewBox="0 0 24 24" aria-hidden>
              <path
                d="M4 4h16a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2H8l-4 3V6a2 2 0 0 1 2-2Z"
                fill="currentColor"
              />
            </svg>
          </Link>

          <Link to="/perfil" className="profile-chip" title="Meu perfil">
            {foto ? (
              <img src={foto} alt="" className="profile-chip__avatar" />
            ) : (
              <span className="profile-chip__fallback">{iniciais || 'C'}</span>
            )}
            <span className="profile-chip__name">{usuario?.nome?.split(' ')[0]}</span>
          </Link>

          <button type="button" className="logout-btn" onClick={handleLogout}>
            Sair
          </button>
        </div>
      </div>
    </header>
  )
}
