import React from 'react'

export default function PoliticaPrivacidadePage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-aviation-gray-50 to-aviation-gray-100">
      <div className="max-w-4xl mx-auto px-4 py-8">
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center space-x-4 mb-6">
            <div className="w-16 h-16 bg-gradient-aviation-red rounded-2xl flex items-center justify-center">
              <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
              </svg>
            </div>
            <div>
              <h1 className="text-3xl font-bold text-gray-900 font-display">Política de Privacidade</h1>
              <p className="text-gray-600 mt-2">Sistema de Plano de Voo - FlightPlan</p>
            </div>
          </div>
        </div>

        {/* Content */}
        <div className="bg-white rounded-2xl shadow-soft-lg p-8 space-y-8">
          {/* Visão Geral */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">📋</span>
              Política de Privacidade — Visão Geral
            </h2>
            <p className="text-gray-700 leading-relaxed">
              Esta Política explica como <strong className="text-aviation-red-600">Eduardo Bregaida</strong> 
              coleta, usa, armazena e compartilha dados pessoais no contexto da Plataforma.
            </p>
          </section>

          {/* Dados Coletados */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">1</span>
              Dados Coletados
            </h2>
            <div className="space-y-4">
              <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
                <h3 className="font-semibold text-blue-900 mb-2">Dados Cadastrais</h3>
                <p className="text-blue-800 text-sm">Nome, e-mail, telefone, códigos ANAC, matrícula da aeronave, etc.</p>
              </div>
              <div className="bg-green-50 border border-green-200 rounded-xl p-4">
                <h3 className="font-semibold text-green-900 mb-2">Dados Operacionais</h3>
                <p className="text-green-800 text-sm">Planos de voo (FPL), rotas, horários, aeródromos, EET, alternados.</p>
              </div>
              <div className="bg-purple-50 border border-purple-200 rounded-xl p-4">
                <h3 className="font-semibold text-purple-900 mb-2">Dados Técnicos</h3>
                <p className="text-purple-800 text-sm">Logs de acesso, endereços IP, navegador, dispositivo e cookies.</p>
              </div>
            </div>
          </section>

          {/* Finalidades */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">2</span>
              Finalidades
            </h2>
            <ul className="space-y-3 text-gray-700">
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                Execução e gerenciamento de planos de voo.
              </li>
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                Comunicação com o usuário (alertas, confirmações, notificações operacionais).
              </li>
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                Melhoria da Plataforma, prevenção a fraudes e segurança.
              </li>
              <li className="flex items-start">
                <svg className="w-5 h-5 text-aviation-red-500 mt-0.5 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                Cumprimento de obrigações legais e regulatórias (compartilhamento com autoridades aeronáuticas quando necessário).
              </li>
            </ul>
          </section>

          {/* Compartilhamento */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">3</span>
              Compartilhamento
            </h2>
            <p className="text-gray-700 leading-relaxed">
              Dados podem ser compartilhados com autoridades competentes (DECEA, ANAC), provedores de infraestrutura, 
              processadores de dados e parceiros que ajudem na operação do serviço. 
              <strong className="text-aviation-red-600"> Não comercializamos dados pessoais.</strong>
            </p>
          </section>

          {/* Segurança e Retenção */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">4</span>
              Segurança e Retenção
            </h2>
            <p className="text-gray-700 leading-relaxed">
              Adotamos medidas técnicas e administrativas razoáveis para proteger dados. Mantemos dados pelo período 
              necessário às finalidades ou conforme exigência legal/regulatória.
            </p>
          </section>

          {/* Exercício de Direitos */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">5</span>
              Exercício de Direitos
            </h2>
            <p className="text-gray-700 leading-relaxed">
            O titular pode solicitar acesso, retificação, eliminação, portabilidade, limitação do tratamento e 
            revogação de consentimento através do e-mail <strong className="text-aviation-red-600">fplbr.aerobatic@gmail.com</strong>. 
            Responderemos conforme prazos legais.
            </p>
          </section>

          {/* Transferência Internacional */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">6</span>
              Transferência Internacional
            </h2>
            <p className="text-gray-700 leading-relaxed">
              Se houver transferência internacional de dados, serão adotadas garantias contratuais adequadas e 
              observadas normas aplicáveis.
            </p>
          </section>

          {/* LGPD */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">📜</span>
              LGPD — Direitos do Titular
            </h2>
            <div className="bg-yellow-50 border border-yellow-200 rounded-xl p-6">
              <h3 className="font-semibold text-yellow-900 mb-3">Seus Direitos</h3>
              <ul className="space-y-2 text-yellow-800">
                <li className="flex items-start">
                  <svg className="w-4 h-4 text-yellow-600 mt-0.5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                  </svg>
                  Acesso aos dados pessoais tratados
                </li>
                <li className="flex items-start">
                  <svg className="w-4 h-4 text-yellow-600 mt-0.5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                  </svg>
                  Correção de dados incompletos, inexatos ou desatualizados
                </li>
                <li className="flex items-start">
                  <svg className="w-4 h-4 text-yellow-600 mt-0.5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                  </svg>
                  Anonimização, bloqueio ou eliminação de dados desnecessários
                </li>
                <li className="flex items-start">
                  <svg className="w-4 h-4 text-yellow-600 mt-0.5 mr-2 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                  </svg>
                  Portabilidade, informação de compartilhamento e revogação do consentimento
                </li>
              </ul>
            <p className="text-yellow-700 text-sm mt-3">
              Pedidos podem ser feitos por e-mail: <strong className="text-yellow-900">fplbr.aerobatic@gmail.com</strong>. 
              Informações sobre prazos e procedimentos serão fornecidas no atendimento.
            </p>
            </div>
          </section>

          {/* Cookies */}
          <section>
            <h2 className="text-2xl font-bold text-gray-900 mb-4 flex items-center">
              <span className="w-8 h-8 bg-gradient-aviation-red text-white rounded-lg flex items-center justify-center text-sm font-bold mr-3">🍪</span>
              Cookies e Tecnologias Semelhantes
            </h2>
            <p className="text-gray-700 leading-relaxed mb-4">
              Utilizamos cookies para autenticação, desempenho, preferências e analytics. O usuário pode gerenciar 
              ou bloquear cookies via navegador; note que isso pode impactar funcionalidades.
            </p>
            <div className="grid md:grid-cols-3 gap-4">
              <div className="bg-red-50 border border-red-200 rounded-xl p-4">
                <h3 className="font-semibold text-red-900 mb-2">Essenciais</h3>
                <p className="text-red-800 text-sm">Necessários para segurança e autenticação</p>
              </div>
              <div className="bg-blue-50 border border-blue-200 rounded-xl p-4">
                <h3 className="font-semibold text-blue-900 mb-2">Funcionais</h3>
                <p className="text-blue-800 text-sm">Preferências de UI e idioma</p>
              </div>
              <div className="bg-green-50 border border-green-200 rounded-xl p-4">
                <h3 className="font-semibold text-green-900 mb-2">Analíticos</h3>
                <p className="text-green-800 text-sm">Métricas e performance (ex.: Google Analytics)</p>
              </div>
            </div>
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
