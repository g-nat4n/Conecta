import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { Loading } from '../components/ui/Loading'

export function ProtectedRoute() {
  const { token, carregando } = useAuth()
  const location = useLocation()

  if (carregando) {
    return <Loading label="Validando sessão..." />
  }

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  return <Outlet />
}
