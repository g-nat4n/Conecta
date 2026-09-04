import './Loading.css'

export function Loading({ label = 'Carregando...' }: { label?: string }) {
  return (
    <div className="loading" role="status" aria-live="polite">
      <div className="loading-ring" />
      <p>{label}</p>
    </div>
  )
}
