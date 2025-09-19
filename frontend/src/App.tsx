import { Routes, Route, Link, useLocation } from 'react-router-dom'
import Home from './pages/Home'
import PlanoDeVooForm from './pages/PlanoDeVooForm'
import PlanoDeVooView from './pages/PlanoDeVooView'
import { FlightPlanList } from './pages/FlightPlanList'
import { Layout } from './components/Layout'
import LoginPage from './pages/Login'
import CadastroPage from './pages/Cadastro'
import EsqueciSenhaPage from './pages/EsqueciSenha'
import TrocaSenhaPage from './pages/TrocaSenha'

export default function App() {
  const location = useLocation()
  return (
    <Layout>
      <Routes location={location}>
        <Route path="/" element={<Home />} />
        <Route path="/flightplan/novo" element={<PlanoDeVooForm />} />
        <Route path="/flightplan/:id" element={<PlanoDeVooView />} />
        <Route path="/flightplan/listar" element={<FlightPlanList />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/cadastro" element={<CadastroPage />} />
        <Route path="/esqueci-senha" element={<EsqueciSenhaPage />} />
        <Route path="/troca-senha" element={<TrocaSenhaPage />} />
        <Route
          path="*"
          element={
            <div className="card text-center">
              <h2 className="subtitle mb-4">Página não encontrada</h2>
              <p className="text-gray-600 mb-6">A rota que você acessou não existe.</p>
              <div className="flex flex-col sm:flex-row gap-4 justify-center">
                <Link to="/" className="btn btn-primary">
                  Voltar ao Início
                </Link>
                <Link to="/flightplan/novo" className="btn btn-ghost">
                  Criar Novo FPL
                </Link>
              </div>
            </div>
          }
        />
      </Routes>
    </Layout>
  )
}
