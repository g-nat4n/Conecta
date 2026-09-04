import { PostCard } from './PostCard'
import type { Post } from '../../types/post'
import { Loading } from '../ui/Loading'
import './PostList.css'

interface PostListProps {
  posts: Post[]
  loading?: boolean
  onChanged: () => void
}

export function PostList({ posts, loading = false, onChanged }: PostListProps) {
  if (loading) {
    return <Loading label="Carregando feed..." />
  }

  if (posts.length === 0) {
    return (
      <div className="post-list-empty glass-panel">
        <h3>Nenhuma publicação ainda</h3>
        <p>Seja o primeiro a compartilhar algo no Conecta.</p>
      </div>
    )
  }

  return (
    <div className="post-list">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} onChanged={onChanged} />
      ))}
    </div>
  )
}
