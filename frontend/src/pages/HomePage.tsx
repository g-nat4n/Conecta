import { useCallback, useEffect, useState } from 'react'
import { CreatePost } from '../components/feed/CreatePost'
import { PostList } from '../components/feed/PostList'
import { UserProfileCard } from '../components/feed/UserProfileCard'
import { useAuth } from '../contexts/AuthContext'
import { listarPosts } from '../services/postService'
import type { Post } from '../types/post'
import { getErrorMessage } from '../utils/error'
import { mediaUrl } from '../utils/media'
import './HomePage.css'

export function HomePage() {
  const { usuario } = useAuth()
  const [posts, setPosts] = useState<Post[]>([])
  const [loading, setLoading] = useState(true)
  const [erro, setErro] = useState('')
  const [statsKey, setStatsKey] = useState(0)

  const carregar = useCallback(async () => {
    try {
      setErro('')
      const page = await listarPosts(0, 30)
      setPosts(page.content)
      setStatsKey((v) => v + 1)
    } catch (error) {
      setErro(getErrorMessage(error, 'Não foi possível carregar o feed'))
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    void carregar()
  }, [carregar])

  if (!usuario) return null

  return (
    <div className="home-layout">
      <div className="home-layout__left">
        <UserProfileCard usuario={usuario} refreshKey={statsKey} />
      </div>

      <main className="home-layout__center">
        <CreatePost
          nomeUsuario={usuario.nome}
          fotoUsuario={mediaUrl(usuario.foto)}
          onCreated={() => void carregar()}
        />
        {erro ? <div className="home-alert">{erro}</div> : null}
        <PostList posts={posts} loading={loading} onChanged={() => void carregar()} />
      </main>

      <aside className="home-layout__right glass-panel">
        <h3>Em breve</h3>
        <p>Sugestões de amigos, tendências e atalhos rápidos vão aparecer nesta coluna.</p>
      </aside>
    </div>
  )
}
