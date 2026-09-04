import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import logo from '../assets/logo-branca.png'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { useAuth } from '../contexts/AuthContext'
import { getErrorMessage } from '../utils/error'
import './AuthPages.css'

interface FormErrors {
  nome?: string
  email?: string
  senha?: string
  confirmarSenha?: string
}

export function RegistroPage() {
  const { registrar } = useAuth()
  const navigate = useNavigate()
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [confirmarSenha, setConfirmarSenha] = useState('')
  const [errors, setErrors] = useState<FormErrors>({})
  const [erroGeral, setErroGeral] = useState('')
  const [loading, setLoading] = useState(false)

  function validar(): boolean {
    const next: FormErrors = {}

    if (!nome.trim()) next.nome = 'O nome é obrigatório'
    if (!email.trim()) next.email = 'O email é obrigatório'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim())) next.email = 'Informe um email válido'
    if (!senha) next.senha = 'A senha é obrigatória'
    else if (senha.length < 6) next.senha = 'A senha deve ter no mínimo 6 caracteres'
    if (!confirmarSenha) next.confirmarSenha = 'Confirme a senha'
    else if (senha !== confirmarSenha) next.confirmarSenha = 'As senhas não conferem'

    setErrors(next)
    return Object.keys(next).length === 0
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setErroGeral('')
    if (!validar()) return

    setLoading(true)
    try {
      await registrar({
        nome: nome.trim(),
        email: email.trim(),
        senha,
        confirmarSenha,
      })
      navigate('/login', { replace: true, state: { registrado: true } })
    } catch (error) {
      setErroGeral(getErrorMessage(error, 'Não foi possível criar a conta'))
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card glass-panel" onSubmit={handleSubmit} noValidate>
        <img src={logo} alt="Conecta" className="auth-card__logo" />
        <h1>Criar conta</h1>
        <p className="auth-card__subtitle">Comece sua jornada no Conecta.</p>

        <Input
          label="Nome"
          name="nome"
          autoComplete="name"
          value={nome}
          onChange={(e) => setNome(e.target.value)}
          placeholder="Seu nome"
          error={errors.nome}
          required
        />

        <Input
          label="Email"
          type="email"
          name="email"
          autoComplete="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="seu@email.com"
          error={errors.email}
          required
        />

        <Input
          label="Senha"
          type="password"
          name="senha"
          autoComplete="new-password"
          value={senha}
          onChange={(e) => setSenha(e.target.value)}
          placeholder="Mínimo 6 caracteres"
          error={errors.senha}
          required
        />

        <Input
          label="Confirmar senha"
          type="password"
          name="confirmarSenha"
          autoComplete="new-password"
          value={confirmarSenha}
          onChange={(e) => setConfirmarSenha(e.target.value)}
          placeholder="Repita a senha"
          error={errors.confirmarSenha}
          required
        />

        {erroGeral ? <div className="auth-alert">{erroGeral}</div> : null}

        <Button type="submit" fullWidth loading={loading}>
          Cadastrar
        </Button>

        <p className="auth-card__footer">
          Já tem conta? <Link to="/login">Entrar</Link>
        </p>
      </form>
    </div>
  )
}
