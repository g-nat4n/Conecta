import { useRef, useState, type FormEvent } from 'react'
import { Button } from '../ui/Button'
import { criarPost } from '../../services/postService'
import { getErrorMessage } from '../../utils/error'
import './CreatePost.css'

interface CreatePostProps {
  nomeUsuario: string
  fotoUsuario?: string | null
  onCreated: () => void
}

export function CreatePost({ nomeUsuario, fotoUsuario, onCreated }: CreatePostProps) {
  const [conteudo, setConteudo] = useState('')
  const [imagem, setImagem] = useState<File | null>(null)
  const [preview, setPreview] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)
  const [erro, setErro] = useState('')
  const fileRef = useRef<HTMLInputElement>(null)
  const primeiroNome = nomeUsuario.split(' ')[0]

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    if (!conteudo.trim()) {
      setErro('Escreva algo para publicar')
      return
    }

    setLoading(true)
    setErro('')
    try {
      await criarPost(conteudo.trim(), imagem)
      setConteudo('')
      setImagem(null)
      setPreview(null)
      if (fileRef.current) fileRef.current.value = ''
      onCreated()
    } catch (error) {
      setErro(getErrorMessage(error, 'Não foi possível publicar'))
    } finally {
      setLoading(false)
    }
  }

  function onFile(file?: File | null) {
    if (!file) return
    setImagem(file)
    setPreview(URL.createObjectURL(file))
  }

  return (
    <form className="create-post glass-panel" onSubmit={handleSubmit}>
      <div className="create-post__row">
        {fotoUsuario ? (
          <img src={fotoUsuario} alt="" className="create-post__avatar" />
        ) : (
          <div className="create-post__avatar create-post__avatar--fallback" aria-hidden>
            {primeiroNome.charAt(0).toUpperCase()}
          </div>
        )}
        <textarea
          value={conteudo}
          onChange={(e) => setConteudo(e.target.value)}
          placeholder={`No que você está pensando, ${primeiroNome}?`}
          rows={3}
          maxLength={2000}
        />
      </div>

      {preview ? (
        <div className="create-post__preview">
          <img src={preview} alt="Pré-visualização" />
          <button
            type="button"
            onClick={() => {
              setImagem(null)
              setPreview(null)
              if (fileRef.current) fileRef.current.value = ''
            }}
          >
            Remover imagem
          </button>
        </div>
      ) : null}

      <div className="create-post__actions">
        <button type="button" className="create-post__chip" onClick={() => fileRef.current?.click()}>
          Foto
        </button>
        <input
          ref={fileRef}
          type="file"
          accept="image/png,image/jpeg,image/webp,image/gif"
          hidden
          onChange={(e) => onFile(e.target.files?.[0])}
        />
        <div className="create-post__spacer" />
        <Button type="submit" loading={loading} disabled={!conteudo.trim()}>
          Publicar
        </Button>
      </div>

      {erro ? <p className="create-post__error">{erro}</p> : null}
    </form>
  )
}
