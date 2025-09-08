import { Routes, Route, Navigate, Link, useLocation } from 'react-router-dom'
import PlanoDeVooForm from './pages/PlanoDeVooForm'
import { FlightPlanList } from './pages/FlightPlanList'
import { Layout } from './components/Layout'

export default function App() {
  const location = useLocation()
  return (
    <Layout>
      <Routes location={location}>
        <Route path="/" element={<Navigate to="/flightplan/novo" replace />} />
        <Route path="/flightplan/novo" element={<PlanoDeVooForm />} />
        <Route path="/flightplan/listar" element={<FlightPlanList />} />
        <Route
          path="*"
          element={
            <div className="p-8 text-center">
              <h2 className="text-2xl font-semibold mb-2">Página não encontrada</h2>
              <p className="opacity-80">A rota que você acessou não existe.</p>
              <div className="mt-6">
                <Link to="/flightplan/novo" className="px-4 py-2 rounded-xl shadow bg-indigo-600 text-white">Ir para novo FPL</Link>
              </div>
            </div>
          }
        />
      </Routes>
    </Layout>
  )
}
