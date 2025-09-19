import { Link, NavLink, useLocation } from 'react-router-dom'
import { useState } from 'react'

// Ícones de aviação SVG inline
const HomeIcon = () => (
  <svg className="nav-button-icon" fill="currentColor" viewBox="0 0 24 24">
    <path d="M10,20V14H14V20H19V12H22L12,3L2,12H5V20H10Z"/>
    <path d="M8,12H16V14H8V12Z"/>
  </svg>
)

const PlaneIcon = () => (
  <svg className="nav-button-icon" fill="currentColor" viewBox="0 0 24 24">
    <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
  </svg>
)

const FlightPlanIcon = () => (
  <svg className="nav-button-icon" fill="currentColor" viewBox="0 0 24 24">
    <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
    <path d="M8,12H16V14H8V12M8,16H13V18H8V16Z"/>
  </svg>
)


export function Navbar() {
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const location = useLocation()

  const isActive = (path: string) => location.pathname === path

  return (
    <>
      {/* Header principal - mobile-first */}
      <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-gray-100 shadow-soft">
        <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-gradient-aviation-red rounded-xl flex items-center justify-center relative overflow-hidden">
              <svg className="w-5 h-5 text-aviation-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
              </svg>
              <div className="absolute inset-0 bg-gradient-to-r from-transparent via-aviation-white to-transparent opacity-20 animate-pulse"></div>
            </div>
            <span className="text-xl font-display font-bold text-aviation-navy-900">
              FPL-BR <span className="text-gradient-aviation-red">Pilot</span>
            </span>
          </Link>

          {/* Menu hambúrguer para desktop */}
          <button
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            className="hidden md:flex hamburger"
          >
            <div className="hamburger-line"></div>
            <div className="hamburger-line"></div>
            <div className="hamburger-line"></div>
          </button>
        </div>

        {/* Menu desktop expandido */}
        {isMenuOpen && (
          <div className="hidden md:block border-t border-gray-100 bg-white/95 backdrop-blur-md">
            <div className="max-w-7xl mx-auto px-4 py-4">
              <nav className="flex gap-4">
                <NavLink
                  to="/flightplan/novo"
                  className="btn btn-primary"
                  onClick={() => setIsMenuOpen(false)}
                >
                  <PlaneIcon />
                  Novo Plano de Voo
                </NavLink>
                <NavLink
                  to="/flightplan/listar"
                  className="btn btn-ghost"
                  onClick={() => setIsMenuOpen(false)}
                >
                  <FlightPlanIcon />
                  Meus Planos
                </NavLink>
        </nav>
      </div>
          </div>
        )}
    </header>

      {/* Navegação mobile - botões na parte inferior */}
      <nav className="fixed bottom-0 left-0 right-0 z-40 md:hidden bg-white/95 backdrop-blur-md border-t border-gray-100 shadow-soft-lg">
        <div className="max-w-7xl mx-auto px-4 py-2">
          <div className="grid grid-cols-3 gap-2">
            {/* Home */}
            <NavLink
              to="/"
              className={`nav-button ${isActive('/') ? 'active' : ''}`}
            >
              <HomeIcon />
              <span className="nav-button-text">Hangar</span>
            </NavLink>

            {/* Novo FPL */}
            <NavLink
              to="/flightplan/novo"
              className={`nav-button ${isActive('/flightplan/novo') ? 'active' : ''}`}
            >
              <PlaneIcon />
              <span className="nav-button-text">FPL</span>
            </NavLink>

            {/* Listar FPLs */}
            <NavLink
              to="/flightplan/listar"
              className={`nav-button ${isActive('/flightplan/listar') ? 'active' : ''}`}
            >
              <FlightPlanIcon />
              <span className="nav-button-text">Logbook</span>
            </NavLink>
          </div>
        </div>
      </nav>
    </>
  )
}
