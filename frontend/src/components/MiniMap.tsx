import React, { useEffect, useRef } from 'react'

interface MiniMapProps {
  latitude: number
  longitude: number
  icao: string
  className?: string
}

declare global {
  interface Window {
    L: any
    setMapLatLon: (lat: number, lon: number) => void
  }
}

const MiniMap: React.FC<MiniMapProps> = ({ latitude, longitude, icao, className = '' }) => {
  const mapRef = useRef<HTMLDivElement>(null)
  const mapInstanceRef = useRef<any>(null)
  const markerRef = useRef<any>(null)

  useEffect(() => {
    if (!mapRef.current || !window.L) return

    // Verificar se já existe um mapa válido com as mesmas coordenadas
    if (mapInstanceRef.current && 
        mapInstanceRef.current.getCenter().lat === latitude && 
        mapInstanceRef.current.getCenter().lng === longitude) {
      return
    }

    // Destruir mapa anterior se existir
    if (mapInstanceRef.current) {
      try {
        mapInstanceRef.current.remove()
      } catch (error) {
        console.warn('Erro ao remover mapa anterior:', error)
      }
      mapInstanceRef.current = null
    }

    // Limpar o container antes de criar novo mapa
    if (mapRef.current) {
      mapRef.current.innerHTML = ''
    }

    // Criar novo mapa
    const map = window.L.map(mapRef.current, {
      center: [latitude, longitude],
      zoom: 15,
      zoomControl: false,
      attributionControl: false,
      dragging: true,
      touchZoom: true,
      doubleClickZoom: true,
      scrollWheelZoom: true,
      boxZoom: true,
      keyboard: true
    })

    // Adicionar tile layer do OpenStreetMap
    window.L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map)

    // Criar marcador personalizado para aeródromo
    const airplaneIcon = window.L.divIcon({
      className: 'airplane-marker',
      html: `
        <div style="
          background: #dc2626;
          border: 2px solid white;
          border-radius: 50%;
          width: 20px;
          height: 20px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
          color: white;
          font-weight: bold;
          box-shadow: 0 2px 4px rgba(0,0,0,0.3);
        ">✈</div>
      `,
      iconSize: [20, 20],
      iconAnchor: [10, 10]
    })

    // Adicionar marcador
    const marker = window.L.marker([latitude, longitude], { icon: airplaneIcon })
      .addTo(map)
      .bindPopup(`
        <div style="text-align: center; font-family: Arial, sans-serif;">
          <strong style="color: #dc2626;">🛩️ ${icao}</strong><br>
          <small style="color: #666;">
            Lat: ${latitude.toFixed(6)}<br>
            Lon: ${longitude.toFixed(6)}
          </small>
        </div>
      `)

    // Armazenar referências
    mapInstanceRef.current = map
    markerRef.current = marker

    // Configurar função global de atualização
    window.setMapLatLon = (lat: number, lon: number) => {
      if (map && marker) {
        map.setView([lat, lon], map.getZoom())
        marker.setLatLng([lat, lon])
      }
    }

    // Abrir popup automaticamente
    setTimeout(() => {
      marker.openPopup()
    }, 500)

    // Cleanup
    return () => {
      if (mapInstanceRef.current) {
        try {
          mapInstanceRef.current.remove()
        } catch (error) {
          console.warn('Erro ao remover mapa no cleanup:', error)
        }
        mapInstanceRef.current = null
      }
      if (markerRef.current) {
        markerRef.current = null
      }
    }
  }, [latitude, longitude, icao])

  return (
    <div className={`rotaer-map-container ${className}`}>
      <div 
        ref={mapRef} 
        className="rotaer-map"
        style={{ 
          height: '200px', 
          width: '100%',
          borderRadius: '8px',
          border: '1px solid #d1d5db'
        }}
      />
      <div className="mt-2 text-center">
        <a 
          href={`https://www.openstreetmap.org/?mlat=${latitude}&mlon=${longitude}&zoom=15`}
          target="_blank"
          rel="noopener noreferrer"
          className="text-blue-600 hover:text-blue-800 text-sm underline"
        >
          Abrir no OpenStreetMap
        </a>
      </div>
    </div>
  )
}

export default MiniMap
