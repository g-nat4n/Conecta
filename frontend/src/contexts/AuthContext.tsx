import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { buscarMe, login as loginRequest, registrar as registrarRequest } from '../services/authService'
import type { AuthResponse, LoginPayload, RegistroPayload, Usuario } from '../types/auth'
import {
  clearSession,
  getStoredUser,
  getToken,
  setStoredUser,
  setToken,
} from '../utils/storage'

interface AuthContextValue {
  usuario: Usuario | null
  token: string | null
  carregando: boolean
  login: (payload: LoginPayload) => Promise<void>
  registrar: (payload: RegistroPayload) => Promise<void>
  logout: () => void
  atualizarUsuario: (usuario: Usuario) => void
  aplicarSessao: (response: AuthResponse) => void
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(() => getStoredUser<Usuario>())
  const [token, setTokenState] = useState<string | null>(() => getToken())
  const [carregando, setCarregando] = useState(true)

  useEffect(() => {
    let ativo = true

    async function bootstrap() {
      const storedToken = getToken()
      if (!storedToken) {
        if (ativo) setCarregando(false)
        return
      }

      try {
        const me = await buscarMe()
        if (!ativo) return
        setUsuario(me)
        setStoredUser(me)
        setTokenState(storedToken)
      } catch {
        if (!ativo) return
        clearSession()
        setUsuario(null)
        setTokenState(null)
      } finally {
        if (ativo) setCarregando(false)
      }
    }

    void bootstrap()
    return () => {
      ativo = false
    }
  }, [])

  const login = useCallback(async (payload: LoginPayload) => {
    const response = await loginRequest(payload)
    setToken(response.token)
    setStoredUser(response.usuario)
    setTokenState(response.token)
    setUsuario(response.usuario)
  }, [])

  const registrar = useCallback(async (payload: RegistroPayload) => {
    await registrarRequest(payload)
  }, [])

  const logout = useCallback(() => {
    clearSession()
    setUsuario(null)
    setTokenState(null)
  }, [])

  const atualizarUsuario = useCallback((novoUsuario: Usuario) => {
    setUsuario(novoUsuario)
    setStoredUser(novoUsuario)
  }, [])

  const aplicarSessao = useCallback((response: AuthResponse) => {
    setToken(response.token)
    setStoredUser(response.usuario)
    setTokenState(response.token)
    setUsuario(response.usuario)
  }, [])

  const value = useMemo(
    () => ({
      usuario,
      token,
      carregando,
      login,
      registrar,
      logout,
      atualizarUsuario,
      aplicarSessao,
    }),
    [usuario, token, carregando, login, registrar, logout, atualizarUsuario, aplicarSessao],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider')
  }
  return context
}
