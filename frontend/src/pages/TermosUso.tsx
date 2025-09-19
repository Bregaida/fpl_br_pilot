import React from 'react'

export default function TermosUsoPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-aviation-gray-50 to-aviation-gray-100">
      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center space-x-4 mb-6">
            <div className="w-16 h-16 bg-gradient-aviation-red rounded-2xl flex items-center justify-center">
              <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5z"/>
              </svg>
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900 font-display">Termos de Uso</h1>
              <p className="text-gray-600 mt-2">Sistema de Plano de Voo - FlightPlan</p>
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="bg-white rounded-2xl shadow-soft-lg p-8 space-y-8">

          {/* Seção 1 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">1</span>
              Aceitação
            </h2>
            <p className="text-gray-700 leading-relaxed">
              Ao utilizar o sistema de Plano de Voo ("Plataforma"), você concorda com estes Termos de Uso. 
              Caso não concorde, não utilize a Plataforma.
            </p>
          </section>

          {/* Seção 2 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">2</span>
              Objeto
            </h2>
            <p className="text-gray-700 leading-relaxed">
              A Plataforma tem por finalidade auxiliar no preenchimento, gestão e envio de planos de voo (FPL) 
              e informações relacionadas. A Plataforma não substitui obrigações legais do operador, piloto ou 
              responsável perante autoridades aeronáuticas (DECEA, ANAC, órgãos locais).
            </p>
          </section>

          {/* Seção 3 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">3</span>
              Cadastro e Acesso
            </h2>
            <ul className="space-y-3 text-gray-700">
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                O usuário deve fornecer informações verdadeiras e atualizadas.
              </li>
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                O acesso é individual. O usuário é responsável por manter suas credenciais confidenciais.
              </li>
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                Suspensão ou exclusão de contas poderá ocorrer em caso de violação destes Termos.
              </li>
            </ul>
          </section>

          {/* Seção 4 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">4</span>
              Uso e Responsabilidades
            </h2>
            <ul className="space-y-3 text-gray-700">
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                O usuário é o único responsável pela precisão dos dados inseridos no FPL.
              </li>
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                A Plataforma fornece ferramentas de apoio e validação, mas não garante aceitação ou conformidade automática junto às autoridades.
              </li>
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                É proibido o uso da Plataforma para finalidades ilegais ou negligentes.
              </li>
            </ul>
          </section>

          {/* Seção 5 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">5</span>
              Propriedade Intelectual
            </h2>
            <p className="text-gray-700 leading-relaxed">
              O software, documentação, design e conteúdos da Plataforma são propriedade de 
              <strong className="text-aviation-red-600"> Eduardo Bregaida</strong> ou de seus licenciadores. 
              É vedado reproduzir, distribuir ou modificar sem autorização.
            </p>
          </section>

          {/* Seção 6 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">6</span>
              Limitação de Responsabilidade e Indenização
            </h2>
            <p className="text-gray-700 leading-relaxed">
              Na máxima extensão permitida por lei, <strong className="text-aviation-red-600">Eduardo Bregaida</strong> 
              não se responsabiliza por danos indiretos, lucros cessantes, perda de dados ou quaisquer danos 
              decorrentes do uso da Plataforma. O usuário indenizará a empresa por quaisquer reclamações 
              decorrentes de informações falsas, uso indevido ou violações legais.
            </p>
          </section>

          {/* Seção 7 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">7</span>
              Alterações e Suspensão
            </h2>
            <p className="text-gray-700 leading-relaxed">
              A empresa pode alterar ou descontinuar funcionalidades, bem como estes Termos, mediante comunicação. 
              O uso continuado após alteração constitui aceitação.
            </p>
          </section>

          {/* Seção 8 */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">8</span>
              Legislação e Foro
            </h2>
            <p className="text-gray-700 leading-relaxed">
            Estes Termos são regidos pela legislação brasileira. Fica eleito o foro da comarca de 
            <strong className="text-aviation-red-600"> São Paulo/SP</strong> para dirimir controvérsias.
            </p>
          </section>

          {/* Footer */}
          <div className="border-t border-gray-200 pt-6 mt-8">
            <div className="bg-gray-50 rounded-xl p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-3">Contato</h3>
            <p className="text-gray-600 mb-2">
              <strong className="text-aviation-red-600">Eduardo Bregaida</strong> — <span className="text-gray-500">Rua Baia Grande, 744, Bl4, Ap 21, Vila Bela, SP, 03202-000</span>
            </p>
            <p className="text-gray-600 mb-4">
              Email: <span className="text-aviation-red-600">fplbr.aerobatic@gmail.com</span>
            </p>
            <p className="text-sm text-gray-500">
              Última atualização: <strong>19/09/2025</strong>
            </p>
            </div>
          </div>
        </div>

        {/* Back Button */}
        <div className="mt-8 text-center">
          <a 
            href="/cadastro" 
            className="inline-flex items-center px-6 py-3 bg-gradient-aviation-red text-white rounded-xl font-semibold hover:shadow-lg hover:scale-105 transition-all duration-300"
          >
            <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
            </svg>
            Voltar ao Cadastro
          </a>
        </div>
      </div>
    </div>
  )
}
