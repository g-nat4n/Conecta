import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import logo from '../assets/logo-branca.png'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { useAuth } from '../contexts/AuthContext'
import { getErrorMessage } from '../utils/error'
import './AuthPages.css'

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setErro('')
    setLoading(true)

    try {
      await login({ email: email.trim(), senha })
      navigate('/', { replace: true })
    } catch (error) {
      setErro(getErrorMessage(error, 'Não foi possível entrar'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card glass-panel" onSubmit={handleSubmit} noValidate>
        <img src={logo} alt="Conecta" className="auth-card__logo" />
        <h1>Entrar</h1>
        <p className="auth-card__subtitle">Conecte-se com pessoas e momentos.</p>

        <Input
          label="Email"
          type="email"
          name="email"
          autoComplete="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="seu@email.com"
          required
        />

        <Input
          label="Senha"
          type="password"
          name="senha"
          autoComplete="current-password"
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          placeholder="••••••••"
          required
        />

        {erro ? <div className="auth-alert">{erro}</div> : null}

        <Button type="submit" fullWidth loading={loading}>
          Entrar
        </Button>

        <p className="auth-card__footer">
          Ainda não tem conta? <Link to="/registro">Criar conta</Link>
        </p>
      </form>
    </div>
  )
}
