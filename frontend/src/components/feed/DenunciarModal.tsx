import { useState, type FormEvent } from 'react'
import { criarDenuncia, type TipoDenuncia } from '../../services/denunciaService'
import { getErrorMessage } from '../../utils/error'
import { Modal } from '../ui/Modal'
import './DenunciarModal.css'

interface DenunciarModalProps {
  open: boolean
  tipo: TipoDenuncia
  referenciaId: number
  onClose: () => void
  onSuccess?: () => void
}

const MOTIVOS_SUGESTAO = [
  'Spam ou propaganda',
  'Conteúdo ofensivo',
  'Assédio ou bullying',
  'Informação falsa',
  'Outro',
]

export function DenunciarModal({ open, tipo, referenciaId, onClose, onSuccess }: DenunciarModalProps) {
  const [motivoSelecionado, setMotivoSelecionado] = useState(MOTIVOS_SUGESTAO[0])
  const [detalhe, setDetalhe] = useState('')
  const [erro, setErro] = useState('')
  const [loading, setLoading] = useState(false)

  const titulo = tipo === 'POST' ? 'Denunciar publicação' : 'Denunciar comentário'

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    const motivo =
      motivoSelecionado === 'Outro'
        ? detalhe.trim()
        : detalhe.trim()
          ? `${motivoSelecionado}: ${detalhe.trim()}`
          : motivoSelecionado

    if (motivo.length < 5) {
      setErro('Descreva o motivo com pelo menos 5 caracteres.')
      return
    }

    setLoading(true)
    setErro('')
    try {
      await criarDenuncia({ tipo, referenciaId, motivo })
      setDetalhe('')
      setMotivoSelecionado(MOTIVOS_SUGESTAO[0])
      onSuccess?.()
      onClose()
    } catch (error) {
      setErro(getErrorMessage(error))
    } finally {
      setLoading(false)
    }
  }

  return (
    <Modal open={open} title={titulo} onClose={onClose}>
      <form className="denunciar-form" onSubmit={handleSubmit}>
        <div className="denunciar-form__fields">
          <p className="denunciar-form__hint">
            Sua denúncia será analisada pela moderação. Conte o que aconteceu.
          </p>

          <label className="denunciar-form__label" htmlFor="denuncia-motivo">
            Motivo
          </label>
          <select
            id="denuncia-motivo"
            className="denunciar-form__select"
            value={motivoSelecionado}
            onChange={(e) => setMotivoSelecionado(e.target.value)}
          >
            {MOTIVOS_SUGESTAO.map((item) => (
              <option key={item} value={item}>
                {item}
              </option>
            ))}
          </select>

          <label className="denunciar-form__label" htmlFor="denuncia-detalhe">
            Detalhes {motivoSelecionado === 'Outro' ? '(obrigatório)' : '(opcional)'}
          </label>
          <textarea
            id="denuncia-detalhe"
            className="denunciar-form__textarea"
            value={detalhe}
            onChange={(e) => setDetalhe(e.target.value)}
            rows={3}
            maxLength={500}
            placeholder="Explique o problema..."
          />

          {erro ? <p className="denunciar-form__erro">{erro}</p> : null}
        </div>

        <div className="denunciar-form__actions">
          <button type="button" className="denunciar-form__btn denunciar-form__btn--ghost" onClick={onClose}>
            Cancelar
          </button>
          <button type="submit" className="denunciar-form__btn denunciar-form__btn--primary" disabled={loading}>
            {loading ? 'Enviando...' : 'Enviar denúncia'}
          </button>
        </div>
      </form>
    </Modal>
  )
}
