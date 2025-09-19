import { Link } from 'react-router-dom'

// Ícones de aviação SVG inline
const PlaneIcon = () => (
  <svg className="w-12 h-12 text-white" fill="currentColor" viewBox="0 0 24 24">
    <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
  </svg>
)

const FlightPlanIcon = () => (
  <svg className="w-12 h-12 text-white" fill="currentColor" viewBox="0 0 24 24">
    <path d="M14,2H6A2,2 0 0,0 4,4V20A2,2 0 0,0 6,22H18A2,2 0 0,0 20,20V8L14,2M18,20H6V4H13V9H18V20Z"/>
    <path d="M8,12H16V14H8V12M8,16H13V18H8V16Z"/>
  </svg>
)

const RadarIcon = () => (
  <svg className="w-12 h-12 text-white" fill="currentColor" viewBox="0 0 24 24">
    <circle cx="12" cy="12" r="10" fill="none" stroke="currentColor" strokeWidth="2"/>
    <circle cx="12" cy="12" r="6" fill="none" stroke="currentColor" strokeWidth="2"/>
    <circle cx="12" cy="12" r="2" fill="currentColor"/>
    <path d="M12 2L12 22" stroke="currentColor" strokeWidth="2"/>
    <path d="M2 12L22 12" stroke="currentColor" strokeWidth="2"/>
  </svg>
)

const AcrobaticsIcon = () => (
  <svg className="w-12 h-12 text-white" fill="currentColor" viewBox="0 0 24 24">
    <path d="M12,2C6.48,2 2,6.48 2,12C2,17.52 6.48,22 12,22C17.52,22 22,17.52 22,12C22,6.48 17.52,2 12,2M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,6C8.69,6 6,8.69 6,12C6,15.31 8.69,18 12,18C15.31,18 18,15.31 18,12C18,8.69 15.31,6 12,6M12,16C9.79,16 8,14.21 8,12C8,9.79 9.79,8 12,8C14.21,8 16,9.79 16,12C16,14.21 14.21,16 12,16Z"/>
    <path d="M12,10C10.9,10 10,10.9 10,12C10,13.1 10.9,14 12,14C13.1,14 14,13.1 14,12C14,10.9 13.1,10 12,10Z"/>
  </svg>
)

const CockpitIcon = () => (
  <svg className="w-12 h-12 text-white" fill="currentColor" viewBox="0 0 24 24">
    <path d="M12,2C6.48,2 2,6.48 2,12C2,17.52 6.48,22 12,22C17.52,22 22,17.52 22,12C22,6.48 17.52,2 12,2M12,20C7.59,20 4,16.41 4,12C4,7.59 7.59,4 12,4C16.41,4 20,7.59 20,12C20,16.41 16.41,20 12,20M12,6C8.69,6 6,8.69 6,12C6,15.31 8.69,18 12,18C15.31,18 18,15.31 18,12C18,8.69 15.31,6 12,6M12,16C9.79,16 8,14.21 8,12C8,9.79 9.79,8 12,8C14.21,8 16,9.79 16,12C16,14.21 14.21,16 12,16Z"/>
    <path d="M12,10C10.9,10 10,10.9 10,12C10,13.1 10.9,14 12,14C13.1,14 14,13.1 14,12C14,10.9 13.1,10 12,10Z"/>
    <path d="M8,8H16V10H8V8Z"/>
    <path d="M10,6H14V8H10V6Z"/>
  </svg>
)


const CheckIcon = () => (
  <svg className="w-6 h-6 text-aviation-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
  </svg>
)

export default function Home() {
  return (
    <div className="space-y-8">
      {/* Hero Section */}
      <section className="text-center py-8 md:py-12 relative overflow-hidden">
        {/* Background com gradiente de instrumentos */}
        <div className="absolute inset-0 bg-gradient-instrument opacity-20"></div>
        
        <div className="max-w-4xl mx-auto relative z-10">
          <div className="mb-6">
            <div className="inline-flex items-center gap-2 bg-aviation-white/90 backdrop-blur-sm rounded-full px-4 py-2 mb-4 border border-aviation-red-200">
              <div className="w-3 h-3 bg-aviation-red-600 rounded-full animate-pulse"></div>
              <span className="text-sm font-medium text-aviation-navy-700">Sistema Ativo</span>
            </div>
          </div>
          
          <h1 className="title mb-6">
            Bem-vindo ao <span className="text-gradient-aviation-red">FPL-BR Pilot</span>
          </h1>
          <p className="text-lg md:text-xl text-aviation-navy-600 mb-8 leading-relaxed">
            Sistema profissional para criação e gerenciamento de planos de voo. 
            Desenvolvido para pilotos brasileiros com foco na segurança e conformidade regulatória.
          </p>
          
          {/* CTA Buttons */}
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Link to="/flightplan/novo" className="btn btn-aviation-red text-lg px-8 py-4 fly-in">
              <PlaneIcon />
              Novo Plano de Voo
            </Link>
            <Link to="/flightplan/listar" className="btn btn-aviation-navy text-lg px-8 py-4 fly-in" style={{animationDelay: '0.2s'}}>
              <FlightPlanIcon />
              Logbook
            </Link>
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section className="grid md:grid-cols-3 gap-6">
        {/* Feature 1 - Flight Plan */}
        <div className="aviation-card hover-lift">
          <div className="w-16 h-16 bg-gradient-aviation-red rounded-2xl flex items-center justify-center mb-4 relative">
            <PlaneIcon />
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-aviation-white to-transparent opacity-20 animate-pulse"></div>
          </div>
          <h3 className="subtitle mb-3">Flight Plan</h3>
          <p className="text-aviation-navy-600 leading-relaxed">
            Criação de planos de voo com validação automática e conformidade 
            com as regulamentações da ANAC e ICAO.
          </p>
        </div>

        {/* Feature 2 - Navigation */}
        <div className="aviation-card hover-lift">
          <div className="w-16 h-16 bg-gradient-aviation-navy rounded-2xl flex items-center justify-center mb-4 relative">
            <RadarIcon />
            <div className="absolute inset-0 border-2 border-aviation-white/30 rounded-full animate-ping"></div>
          </div>
          <h3 className="subtitle mb-3">Navigation</h3>
          <p className="text-aviation-navy-600 leading-relaxed">
            Sistema de navegação integrado com dados meteorológicos e 
            informações de aeródromos em tempo real.
          </p>
        </div>

        {/* Feature 3 - Logbook */}
        <div className="aviation-card hover-lift">
          <div className="w-16 h-16 bg-gradient-aviation-mixed rounded-2xl flex items-center justify-center mb-4 relative">
            <FlightPlanIcon />
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-aviation-white to-transparent opacity-20 animate-pulse"></div>
          </div>
          <h3 className="subtitle mb-3">Logbook</h3>
          <p className="text-aviation-navy-600 leading-relaxed">
            Registro completo de voos com histórico detalhado e 
            relatórios para conformidade regulatória.
          </p>
        </div>
      </section>

      {/* Benefits Section */}
      <section className="card-gradient relative overflow-hidden">
        {/* Background com gradiente de instrumentos */}
        <div className="absolute inset-0 bg-gradient-instrument opacity-20"></div>
        
        <div className="text-center mb-8 relative z-10">
          <h2 className="subtitle mb-4">Por que escolher o FPL-BR Pilot?</h2>
          <p className="text-aviation-navy-600 max-w-2xl mx-auto">
            Desenvolvido especificamente para pilotos brasileiros, nosso sistema 
            oferece conformidade total com as regulamentações da ANAC e ICAO.
          </p>
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          <div className="space-y-4">
            <div className="flex items-start gap-3">
              <CheckIcon />
              <div>
                <h4 className="font-semibold text-gray-900 mb-1">Interface Mobile-First</h4>
                <p className="text-gray-600 text-sm">
                  Otimizado para uso em smartphones, tablets e desktops.
                </p>
              </div>
            </div>
            
            <div className="flex items-start gap-3">
              <CheckIcon />
              <div>
                <h4 className="font-semibold text-gray-900 mb-1">Validação em Tempo Real</h4>
                <p className="text-gray-600 text-sm">
                  Feedback instantâneo sobre erros e sugestões de melhoria.
                </p>
              </div>
            </div>
            
            <div className="flex items-start gap-3">
              <CheckIcon />
              <div>
                <h4 className="font-semibold text-gray-900 mb-1">Conformidade ANAC</h4>
                <p className="text-gray-600 text-sm">
                  Atualizado com as mais recentes regulamentações da ANAC.
                </p>
              </div>
            </div>
          </div>

          <div className="space-y-4">
            <div className="flex items-start gap-3">
              <CheckIcon />
              <div>
                <h4 className="font-semibold text-gray-900 mb-1">Backup Automático</h4>
                <p className="text-gray-600 text-sm">
                  Seus dados são salvos automaticamente durante a criação.
                </p>
              </div>
            </div>
            
            <div className="flex items-start gap-3">
              <CheckIcon />
              <div>
                <h4 className="font-semibold text-gray-900 mb-1">Exportação Fácil</h4>
                <p className="text-gray-600 text-sm">
                  Exporte seus planos em formatos padrão da indústria.
                </p>
              </div>
            </div>
            
            <div className="flex items-start gap-3">
              <CheckIcon />
              <div>
                <h4 className="font-semibold text-gray-900 mb-1">Suporte 24/7</h4>
                <p className="text-gray-600 text-sm">
                  Equipe especializada disponível para ajudar quando precisar.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Quick Actions */}
      <section className="text-center">
        <h2 className="subtitle mb-6">Operações</h2>
        <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link to="/flightplan/novo" className="aviation-card hover-lift text-center">
            <div className="w-12 h-12 bg-gradient-aviation-red rounded-xl flex items-center justify-center mx-auto mb-3 relative">
              <PlaneIcon />
              <div className="absolute inset-0 bg-gradient-to-r from-transparent via-aviation-white to-transparent opacity-20 animate-pulse"></div>
            </div>
            <h3 className="font-semibold text-aviation-navy-900 mb-1">Flight Plan</h3>
            <p className="text-sm text-aviation-navy-600">Novo plano de voo</p>
          </Link>

          <Link to="/flightplan/listar" className="aviation-card hover-lift text-center">
            <div className="w-12 h-12 bg-gradient-aviation-navy rounded-xl flex items-center justify-center mx-auto mb-3 relative">
              <FlightPlanIcon />
              <div className="absolute inset-0 border-2 border-aviation-white/30 rounded-full animate-ping"></div>
            </div>
            <h3 className="font-semibold text-aviation-navy-900 mb-1">Logbook</h3>
            <p className="text-sm text-aviation-navy-600">Histórico de voos</p>
          </Link>

          <div className="aviation-card hover-lift text-center opacity-60">
            <div className="w-12 h-12 bg-aviation-gray-300 rounded-xl flex items-center justify-center mx-auto mb-3">
              <AcrobaticsIcon />
            </div>
            <h3 className="font-semibold text-aviation-navy-900 mb-1">Aerobatics</h3>
            <p className="text-sm text-aviation-navy-600">Em breve</p>
          </div>

          <div className="aviation-card hover-lift text-center opacity-60">
            <div className="w-12 h-12 bg-aviation-gray-300 rounded-xl flex items-center justify-center mx-auto mb-3">
              <CockpitIcon />
            </div>
            <h3 className="font-semibold text-aviation-navy-900 mb-1">Cockpit</h3>
            <p className="text-sm text-aviation-navy-600">Em breve</p>
          </div>
        </div>
      </section>
    </div>
  )
}
