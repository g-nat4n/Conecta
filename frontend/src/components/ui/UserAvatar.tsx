import { useState } from 'react'
import { mediaUrl } from '../../utils/media'
import './UserAvatar.css'

interface UserAvatarProps {
  nome?: string | null
  foto?: string | null
  size?: number
  className?: string
}

export function UserAvatar({ nome, foto, size = 42, className = '' }: UserAvatarProps) {
  const [quebrada, setQuebrada] = useState(false)
  const src = mediaUrl(foto)
  const inicial = (nome?.trim()?.charAt(0) || 'C').toUpperCase()

  if (!src || quebrada) {
    return (
      <span
        className={`user-avatar user-avatar--fallback ${className}`.trim()}
        style={{ width: size, height: size, fontSize: size * 0.4 }}
        aria-hidden
      >
        {inicial}
      </span>
    )
  }

  return (
    <img
      className={`user-avatar ${className}`.trim()}
      src={src}
      alt={nome || 'Avatar'}
      width={size}
      height={size}
      loading="lazy"
      referrerPolicy="no-referrer"
      onError={() => setQuebrada(true)}
    />
  )
}
