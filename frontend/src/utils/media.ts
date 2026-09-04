const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

export function mediaUrl(path?: string | null): string | undefined {
  if (!path) return undefined
  const trimmed = path.trim()
  if (!trimmed) return undefined
  if (trimmed.startsWith('http://') || trimmed.startsWith('https://') || trimmed.startsWith('blob:')) {
    return trimmed
  }

  const normalized = trimmed.startsWith('/') ? trimmed : `/${trimmed}`

  // Em desenvolvimento, usa o proxy do Vite (mesma origem) para carregar /uploads sem falha
  if (import.meta.env.DEV) {
    return normalized
  }

  return `${API_URL}${normalized}`
}

export function formatDateTime(value?: string | null): string {
  if (!value) return ''
  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value))
}

export function formatRelative(value?: string | null): string {
  if (!value) return ''
  const date = new Date(value)
  const diffMs = Date.now() - date.getTime()
  const minutes = Math.floor(diffMs / 60000)
  if (minutes < 1) return 'Agora'
  if (minutes < 60) return `${minutes} min`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours} h`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days} d`
  return formatDateTime(value)
}
