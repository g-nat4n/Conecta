import { useRef, useState, type FormEvent } from 'react'
import { Button } from '../components/ui/Button'
import { Input } from '../components/ui/Input'
import { Modal } from '../components/ui/Modal'
import { useAuth } from '../contexts/AuthContext'
import { alterarSenha, atualizarFoto, atualizarPerfil } from '../services/usuarioService'
import { getErrorMessage } from '../utils/error'
import { formatDateTime, mediaUrl } from '../utils/media'
import './PerfilPage.css'

export function PerfilPage() {
  const { usuario, atualizarUsuario, aplicarSessao } = useAuth()
  const fileRef = useRef<HTMLInputElement>(null)

  const [editOpen, setEditOpen] = useState(false)
  const [senhaOpen, setSenhaOpen] = useState(false)
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senhaAtual, setSenhaAtual] = useState('')
  const [novaSenha, setNovaSenha] = useState('')
  const [confirmarNovaSenha, setConfirmarNovaSenha] = useState('')
  const [erro, setErro] = useState('')
  const [sucesso, setSucesso] = useState('')
  const [loading, setLoading] = useState(false)

  if (!usuario) return null

  const foto = mediaUrl(usuario.foto)
  const iniciais = usuario.nome
    .split(' ')
    .slice(0, 2)
    .map((p) => p[0])
    .join('')
    .toUpperCase()

  function abrirEdicao() {
    setNome(usuario!.nome)
    setEmail(usuario!.email)
    setErro('')
    setSucesso('')
    setEditOpen(true)
  }

  async function salvarPerfil(event: FormEvent) {
    event.preventDefault()
    setErro('')
    setLoading(true)
    try {
      const response = await atualizarPerfil({ nome: nome.trim(), email: email.trim() })
      aplicarSessao(response)
      setEditOpen(false)
      setSucesso('Perfil atualizado com sucesso')
    } catch (error) {
      setErro(getErrorMessage(error, 'Não foi possível atualizar o perfil'))
    } finally {
      setLoading(false)
    }
  }

  async function salvarSenha(event: FormEvent) {
    event.preventDefault()
    setErro('')
    if (novaSenha !== confirmarNovaSenha) {
      setErro('A confirmação da nova senha não confere')
      return
    }
    setLoading(true)
    try {
      await alterarSenha({ senhaAtual, novaSenha, confirmarNovaSenha })
      setSenhaOpen(false)
      setSenhaAtual('')
      setNovaSenha('')
      setConfirmarNovaSenha('')
      setSucesso('Senha alterada com sucesso')
    } catch (error) {
      setErro(getErrorMessage(error, 'Não foi possível alterar a senha'))
    } finally {
      setLoading(false)
    }
  }

  async function onFotoChange(file?: File | null) {
    if (!file) return
    setErro('')
    setLoading(true)
    try {
      const atualizado = await atualizarFoto(file)
      atualizarUsuario(atualizado)
      setSucesso('Foto atualizada')
    } catch (error) {
      setErro(getErrorMessage(error, 'Não foi possível atualizar a foto'))
    } finally {
      setLoading(false)
      if (fileRef.current) fileRef.current.value = ''
    }
  }

  return (
    <div className="perfil-page">
      <section className="perfil-card glass-panel">
        <div className="perfil-card__banner" />
        <div className="perfil-card__content">
          <div className="perfil-card__avatar-wrap">
            {foto ? (
              <img src={foto} alt={usuario.nome} className="perfil-card__avatar" />
            ) : (
              <div className="perfil-card__avatar perfil-card__avatar--fallback">{iniciais}</div>
            )}
            <button
              type="button"
              className="perfil-card__photo-btn"
              onClick={() => fileRef.current?.click()}
              disabled={loading}
            >
              Alterar foto
            </button>
            <input
              ref={fileRef}
              type="file"
              accept="image/png,image/jpeg,image/webp,image/gif"
              hidden
              onChange={(e) => void onFotoChange(e.target.files?.[0])}
            />
          </div>

          <h1>{usuario.nome}</h1>
          <p className="perfil-card__email">{usuario.email}</p>
          <p className="perfil-card__meta">Membro desde {formatDateTime(usuario.dataCriacao)}</p>

          {sucesso ? <div className="perfil-alert success">{sucesso}</div> : null}
          {erro && !editOpen && !senhaOpen ? <div className="perfil-alert error">{erro}</div> : null}

          <div className="perfil-card__actions">
            <Button type="button" onClick={abrirEdicao}>
              Editar perfil
            </Button>
            <Button type="button" variant="secondary" onClick={() => { setErro(''); setSenhaOpen(true) }}>
              Alterar senha
            </Button>
          </div>
        </div>
      </section>

      <Modal open={editOpen} title="Editar perfil" onClose={() => setEditOpen(false)}>
        <form onSubmit={salvarPerfil} className="perfil-form">
          <Input label="Nome" value={nome} onChange={(e) => setNome(e.target.value)} required />
          <Input
            label="Email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          {erro ? <div className="perfil-alert error">{erro}</div> : null}
          <Button type="submit" fullWidth loading={loading}>
            Salvar
          </Button>
        </form>
      </Modal>

      <Modal open={senhaOpen} title="Alterar senha" onClose={() => setSenhaOpen(false)}>
        <form onSubmit={salvarSenha} className="perfil-form">
          <Input
            label="Senha atual"
            type="password"
            value={senhaAtual}
            onChange={(e) => setSenhaAtual(e.target.value)}
            required
          />
          <Input
            label="Nova senha"
            type="password"
            value={novaSenha}
            onChange={(e) => setNovaSenha(e.target.value)}
            required
          />
          <Input
            label="Confirmar nova senha"
            type="password"
            value={confirmarNovaSenha}
            onChange={(e) => setConfirmarNovaSenha(e.target.value)}
            required
          />
          {erro ? <div className="perfil-alert error">{erro}</div> : null}
          <Button type="submit" fullWidth loading={loading}>
            Atualizar senha
          </Button>
        </form>
      </Modal>
    </div>
  )
}
