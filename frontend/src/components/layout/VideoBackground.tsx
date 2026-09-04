import { useEffect, useState } from 'react'
import fundoVideo from '../../assets/fundo.mp4'
import './VideoBackground.css'

export function VideoBackground() {
  const [usarFallback, setUsarFallback] = useState(false)

  useEffect(() => {
    const preferReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
    const isSmallScreen = window.matchMedia('(max-width: 768px)').matches
    const saveData = 'connection' in navigator && (navigator as Navigator & {
      connection?: { saveData?: boolean }
    }).connection?.saveData

    if (preferReducedMotion || saveData || isSmallScreen) {
      setUsarFallback(true)
    }
  }, [])

  return (
    <div className="video-bg" aria-hidden>
      {!usarFallback ? (
        <video
          className="video-bg__media"
          autoPlay
          loop
          muted
          playsInline
          preload="metadata"
          onError={() => setUsarFallback(true)}
        >
          <source src={fundoVideo} type="video/mp4" />
        </video>
      ) : (
        <div className="video-bg__fallback" />
      )}
      <div className="video-bg__overlay" />
      <div className="video-bg__glow" />
    </div>
  )
}
