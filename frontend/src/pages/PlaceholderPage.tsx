import { Link } from 'react-router-dom'
import './PlaceholderPage.css'

interface PlaceholderPageProps {
  title: string
  description: string
}

export function PlaceholderPage({ title, description }: PlaceholderPageProps) {
  return (
    <div className="placeholder-page">
      <section className="placeholder-card glass-panel">
        <h1>{title}</h1>
        <p>{description}</p>
        <Link to="/">Voltar ao início</Link>
      </section>
    </div>
  )
}
