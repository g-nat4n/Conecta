import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react'

export interface ToastItem {
  id: string
  mensagem: string
  tipo?: string
  referenciaId?: number
  foto?: string | null
  nome?: string
  onClick?: () => void
}

interface ToastContextValue {
  toasts: ToastItem[]
  pushToast: (toast: Omit<ToastItem, 'id'>) => void
  removeToast: (id: string) => void
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined)

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([])

  const removeToast = useCallback((id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  const pushToast = useCallback(
    (toast: Omit<ToastItem, 'id'>) => {
      const id = `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`
      setToasts((prev) => [...prev, { ...toast, id }])
      window.setTimeout(() => removeToast(id), 4500)
    },
    [removeToast],
  )

  const value = useMemo(() => ({ toasts, pushToast, removeToast }), [toasts, pushToast, removeToast])

  return <ToastContext.Provider value={value}>{children}</ToastContext.Provider>
}

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast deve ser usado dentro de ToastProvider')
  return ctx
}
