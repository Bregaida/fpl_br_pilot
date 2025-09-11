import { Routes, Route, Navigate, Link, useLocation } from 'react-router-dom'
import PlanoDeVooForm from './pages/PlanoDeVooForm'
import PlanoDeVooView from './pages/PlanoDeVooView'
import { FlightPlanList } from './pages/FlightPlanList'
import { Layout } from './components/Layout'
import AerodromosPage from './pages/Aerodromos'
import LoginPage from './pages/Login'
import CadastroPage from './pages/Cadastro'
import EsqueciSenhaPage from './pages/EsqueciSenha'
import TrocaSenhaPage from './pages/TrocaSenha'
import TesteValidacaoPage from './pages/TesteValidacao'
import TesteOnBlurPage from './pages/TesteOnBlur'

export default function App() {
  const location = useLocation()
  return (
    <Layout>
      <Routes location={location}>
        <Route path="/" element={<Navigate to="/flightplan/novo" replace />} />
        <Route path="/flightplan/novo" element={<PlanoDeVooForm />} />
        <Route path="/flightplan/:id" element={<PlanoDeVooView />} />
        <Route path="/flightplan/listar" element={<FlightPlanList />} />
        <Route path="/aerodromos" element={<AerodromosPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/cadastro" element={<CadastroPage />} />
        <Route path="/esqueci-senha" element={<EsqueciSenhaPage />} />
        <Route path="/troca-senha" element={<TrocaSenhaPage />} />
        <Route path="/teste-validacao" element={<TesteValidacaoPage />} />
        <Route path="/teste-onblur" element={<TesteOnBlurPage />} />
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
