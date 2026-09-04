import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { Loading } from '../components/ui/Loading'

export function PublicOnlyRoute() {
  const { token, carregando } = useAuth()

  if (carregando) {
    return <Loading label="Carregando..." />
  }

  if (token) {
    return <Navigate to="/" replace />
  }

  return <Outlet />
}
