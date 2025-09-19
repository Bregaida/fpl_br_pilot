import React from 'react'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AuthAPI, CepAPI } from '../services/auth'
import QrCode from '../components/QrCode'
import TotpInput from '../components/TotpInput'
import PasswordField from '../components/PasswordField'
import TermosModal from '../components/TermosModal'
import PrivacidadeModal from '../components/PrivacidadeModal'
import { cadastroSchema, CadastroFormData } from '../schemas/validation'

export default function CadastroPage() {
  const [step, setStep] = React.useState<'form'|'totp'|'done'>('form')
  const [otpauth, setOtpauth] = React.useState<string>('')
  const [secretMasked, setSecretMasked] = React.useState<string>('')
  const [totp, setTotp] = React.useState<string>('')
  const [msg, setMsg] = React.useState<string>('')
  const [loadingCep, setLoadingCep] = React.useState<boolean>(false)
  const [showTermos, setShowTermos] = React.useState<boolean>(false)
  const [showPrivacidade, setShowPrivacidade] = React.useState<boolean>(false)

  const methods = useForm<CadastroFormData>({
    resolver: zodResolver(cadastroSchema),
    mode: 'onSubmit', // Validação apenas no submit
    reValidateMode: 'onChange', // Revalidação após mudanças
    defaultValues: {
      fullName: '',
      email: '',
      cpf: '',
      telefone: '',
      cep: '',
      address: '',
      addressNumber: '',
      addressComplement: '',
      neighborhood: '',
      city: '',
      uf: '',
      birthDate: '',
      maritalStatus: '',
      pilotType: '',
      company: '',
      loginAlias: '',
      password: '',
      confirmPassword: ''
    }
  })

  const { handleSubmit, setValue, watch } = methods

  async function onCepBlur() {
    const cep = watch('cep')?.replace(/\D/g, '') || ''
    if (cep.length === 8) {
      setLoadingCep(true)
      setMsg('')
      try {
        const { data } = await CepAPI.lookup(cep)
        if (!data.erro) {
          setValue('address', data.logradouro || '')
          setValue('neighborhood', data.bairro || '')
          setValue('city', data.cidade || '')
          setValue('uf', data.uf || '')
          setMsg('')
        } else {
          setMsg(data.mensagem || 'CEP não encontrado')
        }
      } catch (error: any) {
        console.error('Erro ao buscar CEP:', error)
        setMsg('Erro ao buscar CEP. Tente novamente.')
      } finally {
        setLoadingCep(false)
      }
    }
  }

  async function onSubmit(data: CadastroFormData) {
    setMsg('')
    try {
      const digits = data.telefone.replace(/\D/g, '')
      const phoneDdd = digits.slice(0, 2)
      const phoneNumber = digits.slice(2)
      const payload = { ...data, phoneDdd, phoneNumber }
      const { data: response } = await AuthAPI.register(payload)
      setOtpauth(response.otpauthUrl)
      setSecretMasked(response.secretMasked)
      setStep('totp')
    } catch (error: any) {
      setMsg(error?.response?.data?.reason || 'Erro no cadastro')
    }
  }

  async function onVerify() {
    try {
      await AuthAPI.verify2fa(watch('email'), parseInt(totp || '0', 10))
      setStep('done')
    } catch (error: any) {
      setMsg(error?.response?.data?.reason || 'Erro na verificação 2FA')
    }
  }

  const pilotTypeOptions = [
    { value: 'Piloto Aluno', label: 'Piloto Aluno' },
    { value: 'Piloto Comercial', label: 'Piloto Comercial' },
    { value: 'Piloto Privado', label: 'Piloto Privado' },
    { value: 'Piloto Desportivo', label: 'Piloto Desportivo' },
    { value: 'Piloto de Plataforma Virtual', label: 'Piloto de Plataforma Virtual' },
    { value: 'Piloto de Drone', label: 'Piloto de Drone' }
  ]

  return (
    <div className="min-h-screen bg-gradient-to-br from-aviation-gray-50 to-aviation-gray-100">
      {step === 'form' && (
        <div className="flex min-h-screen">
          {/* Seção Hero - Lado Esquerdo */}
          <div className="hidden lg:flex lg:w-2/3 relative overflow-hidden">
            {/* Background com avião */}
            <div className="absolute inset-0 bg-gradient-to-br from-aviation-red-600 via-aviation-red-700 to-aviation-navy-800">
              <div className="absolute inset-0 bg-black/20"></div>
              {/* Ícone de avião decorativo */}
              <div className="absolute top-20 right-20 w-32 h-32 opacity-20">
                <svg viewBox="0 0 24 24" fill="currentColor" className="w-full h-full text-white">
                  <path d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5zM12 2l2 6h-4l2-6zm0 20l-2-6h4l-2 6z"/>
                </svg>
              </div>
            </div>
            
            {/* Conteúdo da seção hero */}
            <div className="relative z-10 flex flex-col justify-center items-center text-center text-white p-12">
              <div className="max-w-md space-y-8">
                <div className="space-y-4">
                  <h1 className="text-4xl font-bold font-display leading-tight">
                    Junte-se ao FlightPlan
                  </h1>
                  <p className="text-xl text-white/90 leading-relaxed">
                    Planeje seus voos com precisão e segurança
                  </p>
                </div>
                
                <div className="space-y-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-6 h-6 bg-white/20 rounded-full flex items-center justify-center">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                      </svg>
                    </div>
                    <span className="text-white/90">Planos de voo profissionais em minutos</span>
                  </div>
                  
                  <div className="flex items-center space-x-3">
                    <div className="w-6 h-6 bg-white/20 rounded-full flex items-center justify-center">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                      </svg>
                    </div>
                    <span className="text-white/90">Atualizações meteorológicas em tempo real</span>
                  </div>
                  
                  <div className="flex items-center space-x-3">
                    <div className="w-6 h-6 bg-white/20 rounded-full flex items-center justify-center">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                      </svg>
                    </div>
                    <span className="text-white/90">Suporte técnico 24/7 para pilotos</span>
                  </div>
                  
                  <div className="flex items-center space-x-3">
                    <div className="w-6 h-6 bg-white/20 rounded-full flex items-center justify-center">
                      <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                      </svg>
                    </div>
                    <span className="text-white/90">Compatível com todos dispositivos</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Seção do Formulário - Lado Direito */}
          <div className="w-full lg:w-1/3 bg-white flex flex-col">
            <div className="flex-1 flex flex-col justify-center p-8 lg:p-12">
              {/* Header */}
              <div className="mb-8">
                <div className="flex items-center space-x-3 mb-6">
                  <div className="w-10 h-10 bg-gradient-aviation-red rounded-xl flex items-center justify-center">
                    <svg className="w-6 h-6 text-white" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5z"/>
                    </svg>
                  </div>
                  <h2 className="text-2xl font-bold text-aviation-red-600 font-display">FlightPlan</h2>
                </div>
                
                <div className="space-y-2">
                  <h3 className="text-2xl font-bold text-gray-900 font-display">Criar Conta</h3>
                  <p className="text-gray-600">Preencha seus dados para começar sua jornada como piloto</p>
                </div>
              </div>

              {/* Formulário */}
        <FormProvider {...methods}>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                  {/* Dados Pessoais */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Dados Pessoais
                    </h4>
                    
                    <div className="space-y-4">
                      <div className="relative">
                        <label htmlFor="fullName" className="block text-xs font-medium text-gray-600 mb-2">
                          Nome Completo <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('fullName')}
                          id="fullName"
                          type="text"
                          placeholder="Ex: João Silva Santos"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.fullName && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.fullName.message}</span>
                          </div>
                        )}
                      </div>

                      <div className="relative">
                        <label htmlFor="email" className="block text-xs font-medium text-gray-600 mb-2">
                          E-mail <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 8l7.89 4.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('email')}
                          id="email"
                          type="email"
                          placeholder="Ex: joao.piloto@email.com"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.email && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.email.message}</span>
                          </div>
                        )}
                      </div>

                      <div className="relative">
                        <label htmlFor="telefone" className="block text-xs font-medium text-gray-600 mb-2">
                          Telefone <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('telefone')}
                          id="telefone"
                          type="tel"
                          placeholder="Ex: (11) 99999-9999"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.telefone && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.telefone.message}</span>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Tipo de Piloto */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Perfil Aeronáutico
                    </h4>
                    
                    <div className="relative">
                      <label htmlFor="pilotType" className="block text-xs font-medium text-gray-600 mb-2">
                        Tipo de Licença <span className="text-red-500">*</span>
                      </label>
                      <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5z"/>
                        </svg>
                      </div>
                      <select
                        {...methods.register('pilotType')}
                        id="pilotType"
                        className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300 appearance-none bg-white"
                      >
                        <option value="">Selecione seu tipo de licença</option>
                        {pilotTypeOptions.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </select>
                      <div className="absolute inset-y-0 right-0 pr-3 flex items-center pointer-events-none">
                        <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"/>
                        </svg>
                      </div>
                      {methods.formState.errors.pilotType && (
                        <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                          <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                            <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                          </svg>
                          <span>{methods.formState.errors.pilotType.message}</span>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Dados Aeronáuticos */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Dados Aeronáuticos
                    </h4>
                    
                    <div className="space-y-4">
                      <div className="relative">
                        <label htmlFor="cpf" className="block text-xs font-medium text-gray-600 mb-2">
                          CPF <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('cpf')}
                          id="cpf"
                          type="text"
                          placeholder="Ex: 123.456.789-00"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.cpf && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.cpf.message}</span>
                          </div>
                        )}
                      </div>

                      <div className="relative">
                        <label htmlFor="birthDate" className="block text-xs font-medium text-gray-600 mb-2">
                          Data de Nascimento <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('birthDate')}
                          id="birthDate"
                          type="date"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.birthDate && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.birthDate.message}</span>
                          </div>
                        )}
                      </div>

                      <div className="relative">
                        <label htmlFor="company" className="block text-xs font-medium text-gray-600 mb-2">
                          Empresa/Escola <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('company')}
                          id="company"
                          type="text"
                          placeholder="Ex: Aeroclube de São Paulo"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.company && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.company.message}</span>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Endereço */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Endereço
                    </h4>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="relative">
                        <label htmlFor="cep" className="block text-xs font-medium text-gray-600 mb-2">
                          CEP <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          {loadingCep ? (
                            <svg className="w-5 h-5 text-aviation-red-500 animate-spin" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                            </svg>
                          ) : (
                            <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                            </svg>
                          )}
                        </div>
                        <input
                          {...methods.register('cep')}
                          id="cep"
                          type="text"
                          placeholder="Ex: 01234-567"
                          onBlur={onCepBlur}
                          disabled={loadingCep}
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
                        />
                        {methods.formState.errors.cep && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.cep.message}</span>
                          </div>
                        )}
                      </div>

                      <div className="relative">
                        <label htmlFor="addressNumber" className="block text-xs font-medium text-gray-600 mb-2">
                          Número
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('addressNumber')}
                          id="addressNumber"
                          type="text"
                          placeholder="Ex: 123"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.addressNumber && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.addressNumber.message}</span>
                          </div>
                        )}
                      </div>
                    </div>

                    <div className="space-y-4">
                      <div className="relative">
                        <label htmlFor="address" className="block text-xs font-medium text-gray-600 mb-2">
                          Logradouro <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('address')}
                          id="address"
                          type="text"
                          placeholder="Ex: Rua das Flores, 123"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.address && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.address.message}</span>
                          </div>
                        )}
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="relative">
                          <label htmlFor="neighborhood" className="block text-xs font-medium text-gray-600 mb-2">
                            Bairro <span className="text-red-500">*</span>
                          </label>
                          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                            </svg>
                          </div>
                          <input
                            {...methods.register('neighborhood')}
                            id="neighborhood"
                            type="text"
                            placeholder="Ex: Centro"
                            className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                          />
                          {methods.formState.errors.neighborhood && (
                            <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                              </svg>
                              <span>{methods.formState.errors.neighborhood.message}</span>
                            </div>
                          )}
                        </div>

                        <div className="relative">
                          <label htmlFor="city" className="block text-xs font-medium text-gray-600 mb-2">
                            Cidade <span className="text-red-500">*</span>
                          </label>
                          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
                            </svg>
                          </div>
                          <input
                            {...methods.register('city')}
                            id="city"
                            type="text"
                            placeholder="Ex: São Paulo"
                            className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                          />
                          {methods.formState.errors.city && (
                            <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                              </svg>
                              <span>{methods.formState.errors.city.message}</span>
                            </div>
                          )}
                        </div>
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="relative">
                          <label htmlFor="uf" className="block text-xs font-medium text-gray-600 mb-2">
                            UF <span className="text-red-500">*</span>
                          </label>
                          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 21v-4m0 0V5a2 2 0 012-2h6.5l1 1H21l-3 6 3 6h-8.5l-1-1H5a2 2 0 00-2 2zm9-13.5V9"/>
                            </svg>
                          </div>
                          <input
                            {...methods.register('uf')}
                            id="uf"
                            type="text"
                            placeholder="Ex: SP"
                            maxLength={2}
                            className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                          />
                          {methods.formState.errors.uf && (
                            <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                              </svg>
                              <span>{methods.formState.errors.uf.message}</span>
                            </div>
                          )}
                        </div>

                        <div className="relative">
                          <label htmlFor="addressComplement" className="block text-xs font-medium text-gray-600 mb-2">
                            Complemento
                          </label>
                          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                            <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                            </svg>
                          </div>
                          <input
                            {...methods.register('addressComplement')}
                            id="addressComplement"
                            type="text"
                            placeholder="Ex: Apto 45, Bloco B"
                            className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                          />
                          {methods.formState.errors.addressComplement && (
                            <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                              </svg>
                              <span>{methods.formState.errors.addressComplement.message}</span>
                            </div>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Segurança */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Segurança da Conta
                    </h4>
                    
                    <div className="space-y-4">
                      <PasswordField
                        name="password"
                        placeholder="Ex: MinhaSenh@123"
                        icon={
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                          </svg>
                        }
                      />

                      <div className="relative">
                        <label htmlFor="confirmPassword" className="block text-xs font-medium text-gray-600 mb-2">
                          Repetir Senha <span className="text-red-500">*</span>
                        </label>
                        <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                          </svg>
                        </div>
                        <input
                          {...methods.register('confirmPassword')}
                          id="confirmPassword"
                          type="password"
                          placeholder="Confirme sua senha"
                          className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300"
                        />
                        {methods.formState.errors.confirmPassword && (
                          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
                            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                            </svg>
                            <span>{methods.formState.errors.confirmPassword.message}</span>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Termos */}
                  <div className="flex items-start space-x-3">
                    <input
                      type="checkbox"
                      id="terms"
                      className="mt-1 w-4 h-4 text-aviation-red-600 border-gray-300 rounded focus:ring-aviation-red-500"
              required 
            />
                    <label htmlFor="terms" className="text-sm text-gray-600">
                      Eu aceito os{' '}
                      <button 
                        type="button"
                        onClick={() => setShowTermos(true)}
                        className="text-aviation-red-600 hover:text-aviation-red-700 underline bg-transparent border-none p-0 cursor-pointer"
                      >
                        Termos de Uso
                      </button>{' '}
                      e a{' '}
                      <button 
                        type="button"
                        onClick={() => setShowPrivacidade(true)}
                        className="text-aviation-red-600 hover:text-aviation-red-700 underline bg-transparent border-none p-0 cursor-pointer"
                      >
                        Política de Privacidade
                      </button>
                    </label>
                  </div>

                  {/* Mensagem de erro */}
                  {msg && (
                    <div className="p-4 bg-red-50 border border-red-200 rounded-xl">
                      <div className="flex items-center">
                        <svg className="w-5 h-5 text-red-600 mr-2" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd"/>
                        </svg>
                        <span className="text-red-700 text-sm">{msg}</span>
                      </div>
                    </div>
                  )}

                  {/* Botão de cadastro */}
                  <button
                    type="submit"
                    className="w-full bg-gradient-aviation-red text-white py-3 px-6 rounded-xl font-semibold text-lg hover:shadow-lg hover:scale-105 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-aviation-red-500 focus:ring-offset-2"
                  >
                    Cadastrar
            </button>

                  {/* Link para login */}
                  <div className="text-center">
                    <p className="text-gray-600">
                      Já tem uma conta?{' '}
                      <a href="/login" className="text-aviation-red-600 hover:text-aviation-red-700 font-semibold">
                        Faça login
                      </a>
                    </p>
                  </div>
          </form>
        </FormProvider>
            </div>

            {/* Footer */}
            <div className="px-8 py-6 border-t border-gray-200">
              <div className="flex items-center justify-between text-sm text-gray-500">
                <span>© 2024 FlightPlan. Todos os direitos reservados.</span>
                <div className="flex space-x-4">
                  <button type="button" className="hover:text-aviation-red-600 bg-transparent border-none p-0">Suporte</button>
                  <span>|</span>
                  <button type="button" className="hover:text-aviation-red-600 bg-transparent border-none p-0">Contato</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {step==='totp' && (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-aviation-gray-50 to-aviation-gray-100 p-4">
          <div className="max-w-md w-full bg-white rounded-2xl shadow-soft-lg p-8">
            <div className="text-center mb-8">
              <div className="w-16 h-16 bg-gradient-aviation-red rounded-2xl flex items-center justify-center mx-auto mb-4">
                <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5z"/>
                </svg>
              </div>
              <h2 className="text-2xl font-bold text-gray-900 font-display mb-2">Configuração 2FA</h2>
              <p className="text-gray-600">Configure a autenticação de dois fatores para maior segurança</p>
            </div>
            
            <div className="space-y-6">
          <QrCode otpauthUrl={otpauth} />
              <div className="p-4 bg-gray-50 rounded-xl">
                <p className="text-sm text-gray-600 mb-2">Código secreto:</p>
                <code className="text-sm font-mono text-gray-800 break-all">{secretMasked}</code>
              </div>
          <TotpInput value={totp} onChange={setTotp} />
              <button 
                className="w-full bg-gradient-aviation-red text-white py-3 px-6 rounded-xl font-semibold hover:shadow-lg hover:scale-105 transition-all duration-300" 
                onClick={onVerify}
              >
                Confirmar 2FA
              </button>
            </div>
          </div>
        </div>
      )}

      {step==='done' && (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-aviation-gray-50 to-aviation-gray-100 p-4">
          <div className="max-w-md w-full bg-white rounded-2xl shadow-soft-lg p-8 text-center">
            <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-emerald-600 rounded-2xl flex items-center justify-center mx-auto mb-6">
              <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-900 font-display mb-4">Cadastro Concluído!</h2>
            <p className="text-gray-600 mb-6">Sua conta foi criada com sucesso. Agora você pode fazer login e começar a planejar seus voos.</p>
            <a 
              href="/login" 
              className="inline-flex items-center px-6 py-3 bg-gradient-aviation-red text-white rounded-xl font-semibold hover:shadow-lg hover:scale-105 transition-all duration-300"
            >
              Ir para Login
            </a>
          </div>
        </div>
      )}

      {/* Modais */}
      <TermosModal 
        isOpen={showTermos} 
        onClose={() => setShowTermos(false)} 
      />
      <PrivacidadeModal 
        isOpen={showPrivacidade} 
        onClose={() => setShowPrivacidade(false)} 
      />
    </div>
  )
}


