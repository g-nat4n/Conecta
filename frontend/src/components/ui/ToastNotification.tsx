import { useToast } from '../../contexts/ToastContext'
import { UserAvatar } from './UserAvatar'
import './ToastNotification.css'

export function ToastNotification() {
  const { toasts, removeToast } = useToast()

  return (
    <div className="toast-stack" aria-live="polite">
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className="toast-item"
          role="status"
          onClick={() => {
            toast.onClick?.()
            removeToast(toast.id)
          }}
        >
          <UserAvatar nome={toast.nome} foto={toast.foto} size={36} />
          <p>{toast.mensagem}</p>
          <button
            type="button"
            aria-label="Fechar"
            onClick={(e) => {
              e.stopPropagation()
              removeToast(toast.id)
            }}
          >
            ×
          </button>
        </div>
      ))}
    </div>
  )
}
