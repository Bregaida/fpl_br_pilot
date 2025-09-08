import { Link, NavLink } from 'react-router-dom'

export function Navbar() {
  const base = 'px-4 py-2 rounded-2xl hover:bg-slate-200 transition'
  const active = 'bg-slate-200'
  return (
    <header className="sticky top-0 z-30 backdrop-blur bg-white/80 border-b border-slate-200">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/" className="text-xl font-bold tracking-tight">FPL‑BR <span className="text-indigo-600">Pilot</span></Link>
        <nav className="flex gap-2">
          <NavLink to="/flightplan/novo" className={({isActive}) => `${base} ${isActive ? active : ''}`}>Novo FPL</NavLink>
          <NavLink to="/flightplan/listar" className={({isActive}) => `${base} ${isActive ? active : ''}`}>Listar FPLs</NavLink>
        </nav>
      </div>
    </header>
  )
}
