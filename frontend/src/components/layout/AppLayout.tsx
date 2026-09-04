import { Outlet } from 'react-router-dom'
import { useRealtime } from '../../hooks/useRealtime'
import { Header } from './Header'

export function AppLayout() {
  useRealtime()

  return (
    <>
      <Header />
      <Outlet />
    </>
  )
}
