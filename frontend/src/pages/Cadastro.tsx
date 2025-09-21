import React from 'react'
import { useForm, FormProvider } from 'react-hook-form'
import { AuthAPI, CepAPI } from '../services/auth'
import QrCode from '../components/QrCode'
import TotpInput from '../components/TotpInput'
import PasswordField from '../components/PasswordField'
import TermosModal from '../components/TermosModal'
import PrivacidadeModal from '../components/PrivacidadeModal'
import { useDuplicateCheck } from '../hooks/useDuplicateCheck'
import { CadastroFormData } from '../schemas/validation'

export default function CadastroPage() {
  const [step, setStep] = React.useState<'form'|'totp'|'done'>('form')
  const [otpauth, setOtpauth] = React.useState<string>('')
  const [secretMasked, setSecretMasked] = React.useState<string>('')
  const [totp, setTotp] = React.useState<string>('')
  const [msg, setMsg] = React.useState<string>('')
  const [msgType, setMsgType] = React.useState<'success' | 'error'>('error')
  const [loadingCep, setLoadingCep] = React.useState<boolean>(false)
  const [showTermos, setShowTermos] = React.useState<boolean>(false)
  const [showPrivacidade, setShowPrivacidade] = React.useState<boolean>(false)
  const [enableTOTP, setEnableTOTP] = React.useState<boolean>(true)
  const [totpSessionId, setTotpSessionId] = React.useState<string>('')
  
  // Hook para verificar duplicatas
  const { emailError, cpfError, checkEmail, checkCpf, clearEmailError, clearCpfError } = useDuplicateCheck()

  const methods = useForm<CadastroFormData>({
    mode: 'onSubmit',
    reValidateMode: 'onSubmit',
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
      confirmPassword: '',
      enableTOTP: true,
      acceptTerms: false
    }
  })

  const { handleSubmit, setValue, watch } = methods

  // Funções de máscara
  const formatCPF = (value: string) => {
    const numbers = value.replace(/\D/g, '')
    return numbers.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
  }

  const formatPhone = (value: string) => {
    const numbers = value.replace(/\D/g, '')
    if (numbers.length <= 2) return numbers
    if (numbers.length <= 6) return `(${numbers.slice(0, 2)}) ${numbers.slice(2)}`
    if (numbers.length <= 10) return `(${numbers.slice(0, 2)}) ${numbers.slice(2, 6)}-${numbers.slice(6)}`
    return `(${numbers.slice(0, 2)}) ${numbers.slice(2, 7)}-${numbers.slice(7, 11)}`
  }

  const formatCEP = (value: string) => {
    const numbers = value.replace(/\D/g, '')
    return numbers.replace(/(\d{5})(\d{3})/, '$1-$2')
  }

  const formatDate = (value: string) => {
    const numbers = value.replace(/\D/g, '')
    if (numbers.length <= 2) return numbers
    if (numbers.length <= 4) return `${numbers.slice(0, 2)}/${numbers.slice(2)}`
    return `${numbers.slice(0, 2)}/${numbers.slice(2, 4)}/${numbers.slice(4, 8)}`
  }

  // Funções de validação individual para cada campo
  const validateFullName = (value: string) => {
    if (!value.trim()) return 'O Nome Completo é obrigatório'
    const words = value.trim().split(/\s+/)
    if (words.length < 2) return 'Campo inválido, nome incompleto'
    const isValid = words.every(word => /^[A-ZÁÀÂÃÉÊÍÓÔÕÚÇ][a-záàâãéêíóôõúç]*$/.test(word))
    return isValid ? '' : 'Campo inválido, nome incompleto'
  }

  const validateEmail = (value: string) => {
    if (!value.trim()) return 'O E-mail é obrigatório'
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return emailRegex.test(value) ? '' : 'Campo E-mail inválido'
  }

  const validateCPF = (value: string) => {
    if (!value.trim()) return 'O CPF é obrigatório'
    const cpf = value.replace(/\D/g, '')
    if (cpf.length !== 11) return 'Campo CPF inválido'
    if (/^(\d)\1{10}$/.test(cpf)) return 'Campo CPF inválido'
    return ''
  }

  const validatePhone = (value: string) => {
    if (!value.trim()) return 'O Telefone é obrigatório'
    const phone = value.replace(/\D/g, '')
    if (phone.length < 10 || phone.length > 11) return 'Telefone inválido'
    return ''
  }

  const validateCEP = (value: string) => {
    if (!value.trim()) return 'CEP é campo obrigatório'
    const cep = value.replace(/\D/g, '')
    if (cep.length !== 8) return 'CEP deve ter 8 dígitos'
    return ''
  }

  const validateBirthDate = (value: string) => {
    if (!value.trim()) return 'A data de nascimento é obrigatória'
    const dateRegex = /^(\d{2})\/(\d{2})\/(\d{4})$/
    const match = dateRegex.exec(value)
    if (!match) return 'Data inválida'
    const [, day, month, year] = match
    const date = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    const today = new Date()
    const age = today.getFullYear() - date.getFullYear()
    if (age < 10 || age > 124) return 'Data inválida'
    return ''
  }

  const validateUF = (value: string) => {
    if (!value.trim()) return 'UF é campo obrigatório'
    const validUFs = ['AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO']
    return validUFs.includes(value) ? '' : 'UF inválida'
  }

  const validatePilotType = (value: string) => {
    if (!value.trim()) return 'Tipo de Licença é obrigatório'
    return ''
  }

  const validateCompany = (value: string) => {
    if (!value.trim()) return 'Empresa/Escola é obrigatória'
    return ''
  }

  const validateAddress = (value: string) => {
    if (!value.trim()) return 'Logradouro é campo obrigatório'
    return ''
  }

  const validateNeighborhood = (value: string) => {
    if (!value.trim()) return 'Bairro é campo obrigatório'
    return ''
  }

  const validateCity = (value: string) => {
    if (!value.trim()) return 'Cidade é campo obrigatório'
    return ''
  }

  const validatePassword = (value: string) => {
    if (!value.trim()) return 'A senha é obrigatória'
    if (value.length < 8) return 'A senha deve ter pelo menos 8 caracteres'
    return ''
  }

  const validateConfirmPassword = (value: string) => {
    if (!value.trim()) return 'Confirmação de senha é obrigatória'
    const password = watch('password')
    if (value !== password) return 'Senhas não conferem'
    return ''
  }

  const validateTOTP = (value: boolean) => {
    if (!value) return 'Autenticação de Dois Fatores (TOTP) é obrigatória'
    return ''
  }

  const validateTerms = (value: boolean) => {
    if (!value) return 'Eu aceito os Termos de Uso e a Política de Privacidade é campo obrigatório'
    return ''
  }

  // Função para validação individual de campos
  const handleFieldBlur = async (fieldName: string, value: string | boolean) => {
    let error = ''
    
    switch (fieldName) {
      case 'fullName':
        error = validateFullName(value as string)
        break
      case 'email':
        error = validateEmail(value as string)
        if (!error) {
          await checkEmail(value as string)
        }
        break
      case 'cpf':
        error = validateCPF(value as string)
        if (!error) {
          await checkCpf(value as string)
        }
        break
      case 'telefone':
        error = validatePhone(value as string)
        break
      case 'cep':
        error = validateCEP(value as string)
        break
      case 'birthDate':
        error = validateBirthDate(value as string)
        break
      case 'uf':
        error = validateUF(value as string)
        break
      case 'pilotType':
        error = validatePilotType(value as string)
        break
      case 'company':
        error = validateCompany(value as string)
        break
      case 'address':
        error = validateAddress(value as string)
        break
      case 'neighborhood':
        error = validateNeighborhood(value as string)
        break
      case 'city':
        error = validateCity(value as string)
        break
      case 'password':
        error = validatePassword(value as string)
        break
      case 'confirmPassword':
        error = validateConfirmPassword(value as string)
        break
      case 'enableTOTP':
        error = validateTOTP(value as boolean)
        break
      case 'acceptTerms':
        error = validateTerms(value as boolean)
        break
    }
    
    if (error) {
      methods.setError(fieldName, { message: error })
    } else {
      methods.clearErrors(fieldName)
    }
  }

  // Função para busca de CEP
  const handleCepChange = async (cep: string) => {
    if (cep.replace(/\D/g, '').length === 8) {
      setLoadingCep(true)
      try {
        const response = await CepAPI.getCep(cep)
        if (response) {
          setValue('address', response.logradouro || '')
          setValue('neighborhood', response.bairro || '')
          setValue('city', response.localidade || '')
          setValue('uf', response.uf || '')
        }
      } catch (error) {
        console.error('Erro ao buscar CEP:', error)
      } finally {
        setLoadingCep(false)
      }
    }
  }

  // Função de submit
  const onSubmit = async (data: CadastroFormData) => {
    try {
      // Validação manual de todos os campos
      const fieldsToValidate = [
        { name: 'fullName', value: data.fullName },
        { name: 'email', value: data.email },
        { name: 'cpf', value: data.cpf },
        { name: 'telefone', value: data.telefone },
        { name: 'cep', value: data.cep },
        { name: 'birthDate', value: data.birthDate },
        { name: 'uf', value: data.uf },
        { name: 'pilotType', value: data.pilotType },
        { name: 'company', value: data.company },
        { name: 'address', value: data.address },
        { name: 'neighborhood', value: data.neighborhood },
        { name: 'city', value: data.city },
        { name: 'password', value: data.password },
        { name: 'confirmPassword', value: data.confirmPassword },
        { name: 'enableTOTP', value: data.enableTOTP },
        { name: 'acceptTerms', value: data.acceptTerms }
      ]

      let hasErrors = false
      for (const field of fieldsToValidate) {
        let error = ''
        switch (field.name) {
          case 'fullName':
            error = validateFullName(field.value as string)
            break
          case 'email':
            error = validateEmail(field.value as string)
            break
          case 'cpf':
            error = validateCPF(field.value as string)
            break
          case 'telefone':
            error = validatePhone(field.value as string)
            break
          case 'cep':
            error = validateCEP(field.value as string)
            break
          case 'birthDate':
            error = validateBirthDate(field.value as string)
            break
          case 'uf':
            error = validateUF(field.value as string)
            break
          case 'pilotType':
            error = validatePilotType(field.value as string)
            break
          case 'company':
            error = validateCompany(field.value as string)
            break
          case 'address':
            error = validateAddress(field.value as string)
            break
          case 'neighborhood':
            error = validateNeighborhood(field.value as string)
            break
          case 'city':
            error = validateCity(field.value as string)
            break
          case 'password':
            error = validatePassword(field.value as string)
            break
          case 'confirmPassword':
            error = validateConfirmPassword(field.value as string)
            break
          case 'enableTOTP':
            error = validateTOTP(field.value as boolean)
            break
          case 'acceptTerms':
            error = validateTerms(field.value as boolean)
            break
        }
        
        if (error) {
          methods.setError(field.name, { message: error })
          hasErrors = true
        }
      }

      if (hasErrors) {
        setMsg('Por favor, corrija os erros no formulário')
        setMsgType('error')
        return
      }

      // Se chegou até aqui, todos os campos são válidos
      console.log('Enviando dados para registro:', data)
      const response = await AuthAPI.register(data)
      console.log('Resposta do registro:', response)
      
      // Verificar se a resposta tem a estrutura esperada
      if (response && response.data) {
        setOtpauth(response.data.otpauthUrl || '')
        setSecretMasked(response.data.secretMasked || '')
        setTotpSessionId(response.data.totpSessionId || '')
        console.log('Alterando step para totp')
        setStep('totp')
      } else {
        console.error('Resposta inválida:', response)
        setMsg('Erro na resposta do servidor')
        setMsgType('error')
      }
    } catch (error: any) {
      setMsg(error?.response?.data?.reason || 'Erro no cadastro')
      setMsgType('error')
    }
  }

  // Função de verificação TOTP
  const onVerify = async () => {
    try {
      await AuthAPI.verifyTOTP({ code: totp, sessionId: totpSessionId })
      setMsg('TOTP verificado com sucesso!')
      setMsgType('success')
      setStep('done')
    } catch (error: any) {
      setMsg(error?.response?.data?.reason || 'Erro na verificação 2FA')
      setMsgType('error')
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* Mensagens */}
      {msg && (
        <div className={`fixed top-4 right-4 p-4 rounded-xl border shadow-lg z-50 ${
          msgType === 'success' 
            ? 'bg-green-50 border-green-200 text-green-600' 
            : 'bg-red-50 border-red-200 text-red-600'
        }`}>
          <div className="flex items-center space-x-2">
            <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
              {msgType === 'success' ? (
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              ) : (
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
              )}
            </svg>
            <span>{msg}</span>
          </div>
        </div>
      )}

      {step === 'form' && (
        <div className="flex min-h-screen">
          {/* Seção Hero - Lado Esquerdo */}
          <div className="hidden lg:flex lg:w-2/3 relative overflow-hidden">
            <div className="absolute inset-0 bg-gradient-to-br from-red-600 via-red-700 to-red-800">
              <div className="absolute inset-0 bg-black/20"></div>
              <div className="absolute top-20 right-20 w-32 h-32 opacity-20">
                <svg viewBox="0 0 24 24" fill="currentColor" className="w-full h-full text-white">
                  <path d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5zM12 2l2 6h-4l2-6zm0 20l-2-6h4l-2 6z"/>
                </svg>
              </div>
            </div>
            
            <div className="relative z-10 flex flex-col justify-center items-center text-center text-white p-12">
              <div className="max-w-md space-y-8">
                <div className="space-y-4">
                  <div className="w-16 h-16 bg-white/20 rounded-2xl flex items-center justify-center mx-auto">
                    <svg className="w-8 h-8" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5z"/>
                    </svg>
                  </div>
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
                <div className="flex items-center space-x-3 mb-4">
                  <div className="w-10 h-10 bg-gradient-to-r from-red-600 to-red-700 rounded-xl flex items-center justify-center">
                    <svg className="w-5 h-5 text-white" fill="currentColor" viewBox="0 0 24 24">
                      <path d="M21.5 9.5c-.3-.8-1.1-1.3-1.9-1.3H4.4c-.8 0-1.6.5-1.9 1.3L1 12l1.5 2.5c.3.8 1.1 1.3 1.9 1.3h15.2c.8 0 1.6-.5 1.9-1.3L21 12l.5-2.5z"/>
                    </svg>
                  </div>
                  <div>
                    <h1 className="text-2xl font-bold text-gray-900">FlightPlan</h1>
                    <p className="text-sm text-gray-600">Sistema de Planejamento de Voos</p>
                  </div>
                </div>
                <h2 className="text-2xl font-bold text-gray-900 mb-2">Criar Conta</h2>
                <p className="text-gray-600">Preencha os dados abaixo para se cadastrar</p>
              </div>

              {/* Formulário */}
              <FormProvider {...methods}>
                <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                  {/* Dados Pessoais */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Dados Pessoais
                    </h4>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <label htmlFor="fullName" className="block text-sm font-medium text-gray-700">
                          Nome Completo <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('fullName')}
                          id="fullName"
                          type="text"
                          placeholder="Ex: Eduardo Bregaida"
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                        />
                        {methods.formState.errors.fullName && (
                          <p className="text-sm text-red-600">{methods.formState.errors.fullName.message}</p>
                        )}
                      </div>

                      <div className="space-y-2">
                        <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                          E-mail <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('email')}
                          id="email"
                          type="email"
                          placeholder="Ex: eduardo.bregaida@email.com.br"
                          className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent ${
                            methods.formState.errors.email || emailError ? 'border-red-500' : 'border-gray-300'
                          }`}
                        />
                        {methods.formState.errors.email && (
                          <p className="text-sm text-red-600">{methods.formState.errors.email.message}</p>
                        )}
                        {emailError && (
                          <p className="text-sm text-red-600">{emailError}</p>
                        )}
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <label htmlFor="cpf" className="block text-sm font-medium text-gray-700">
                          CPF <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('cpf')}
                          id="cpf"
                          type="text"
                          placeholder="000.000.000-00"
                          onChange={(e) => {
                            const formatted = formatCPF(e.target.value)
                            methods.setValue('cpf', formatted)
                            clearCpfError()
                          }}
                          className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent ${
                            methods.formState.errors.cpf || cpfError ? 'border-red-500' : 'border-gray-300'
                          }`}
                        />
                        {methods.formState.errors.cpf && (
                          <p className="text-sm text-red-600">{methods.formState.errors.cpf.message}</p>
                        )}
                        {cpfError && (
                          <p className="text-sm text-red-600">{cpfError}</p>
                        )}
                      </div>

                      <div className="space-y-2">
                        <label htmlFor="birthDate" className="block text-sm font-medium text-gray-700">
                          Data de Nascimento <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('birthDate')}
                          id="birthDate"
                          type="text"
                          placeholder="dd/mm/yyyy"
                          onChange={(e) => {
                            const formatted = formatDate(e.target.value)
                            methods.setValue('birthDate', formatted)
                          }}
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                        />
                        {methods.formState.errors.birthDate && (
                          <p className="text-sm text-red-600">{methods.formState.errors.birthDate.message}</p>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Contato */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Contato
                    </h4>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <label htmlFor="telefone" className="block text-sm font-medium text-gray-700">
                          Telefone <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('telefone')}
                          id="telefone"
                          type="text"
                          placeholder="(11) 99999-9999"
                          onChange={(e) => {
                            const formatted = formatPhone(e.target.value)
                            methods.setValue('telefone', formatted)
                          }}
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                        />
                        {methods.formState.errors.telefone && (
                          <p className="text-sm text-red-600">{methods.formState.errors.telefone.message}</p>
                        )}
                      </div>

                      <div className="space-y-2">
                        <label htmlFor="company" className="block text-sm font-medium text-gray-700">
                          Empresa/Escola <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('company')}
                          id="company"
                          type="text"
                          placeholder="Ex: Escola de Aviação XYZ"
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                        />
                        {methods.formState.errors.company && (
                          <p className="text-sm text-red-600">{methods.formState.errors.company.message}</p>
                        )}
                      </div>
                    </div>

                    <div className="space-y-2">
                      <label htmlFor="pilotType" className="block text-sm font-medium text-gray-700">
                        Tipo de Licença <span className="text-red-500">*</span>
                      </label>
                      <select
                        {...methods.register('pilotType')}
                        id="pilotType"
                        className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                      >
                        <option value="">Selecione sua categoria</option>
                        <option value="PA">PA - Piloto Aluno</option>
                        <option value="PP">PPL - Piloto Privado</option>
                        <option value="PC">CPL - Piloto Comercial</option>
                        <option value="PLA">ATPL - Piloto de Linha Aérea</option>
                        <option value="PEXE">EXE - Piloto Executivo</option>
                        <option value="INV">INVA - Instrutor de Voo</option>
                        <option value="ACRO">ACRO - Piloto de Acrobacias Aéreas</option>
                        <option value="EXA">EXA - Examinador de Voo</option>
                        <option value="PPLA">PPLA - Piloto de Planadores</option>
                        <option value="PBA">PBA - Piloto de Balão</option>
                        <option value="PUL">PUL - Piloto de Ultraleve</option>
                        <option value="PAGA">PAGA - Piloto Agrícola</option>
                        <option value="PAD">PAD - Piloto Desportivo</option>
                        <option value="PD">PD - Piloto Drone</option>
                        <option value="IVAO">IVAO - Piloto Simulação</option>
                      </select>
                      {methods.formState.errors.pilotType && (
                        <p className="text-sm text-red-600">{methods.formState.errors.pilotType.message}</p>
                      )}
                    </div>
                  </div>

                  {/* Endereço */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Endereço
                    </h4>
                    
                    <div className="space-y-4">
                      <div className="space-y-2">
                        <label htmlFor="cep" className="block text-sm font-medium text-gray-700">
                          CEP <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('cep')}
                          id="cep"
                          type="text"
                          placeholder="00000-000"
                          onChange={(e) => {
                            const formatted = formatCEP(e.target.value)
                            methods.setValue('cep', formatted)
                            if (formatted.replace(/\D/g, '').length === 8) {
                              handleCepChange(formatted)
                            }
                          }}
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                        />
                        {methods.formState.errors.cep && (
                          <p className="text-sm text-red-600">{methods.formState.errors.cep.message}</p>
                        )}
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <label htmlFor="address" className="block text-sm font-medium text-gray-700">
                            Logradouro <span className="text-red-500">*</span>
                          </label>
                          <input
                            {...methods.register('address')}
                            id="address"
                            type="text"
                            placeholder="Rua, Avenida, etc."
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                          />
                          {methods.formState.errors.address && (
                            <p className="text-sm text-red-600">{methods.formState.errors.address.message}</p>
                          )}
                        </div>

                        <div className="space-y-2">
                          <label htmlFor="neighborhood" className="block text-sm font-medium text-gray-700">
                            Bairro <span className="text-red-500">*</span>
                          </label>
                          <input
                            {...methods.register('neighborhood')}
                            id="neighborhood"
                            type="text"
                            placeholder="Nome do bairro"
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                          />
                          {methods.formState.errors.neighborhood && (
                            <p className="text-sm text-red-600">{methods.formState.errors.neighborhood.message}</p>
                          )}
                        </div>
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div className="space-y-2">
                          <label htmlFor="city" className="block text-sm font-medium text-gray-700">
                            Cidade <span className="text-red-500">*</span>
                          </label>
                          <input
                            {...methods.register('city')}
                            id="city"
                            type="text"
                            placeholder="Nome da cidade"
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                          />
                          {methods.formState.errors.city && (
                            <p className="text-sm text-red-600">{methods.formState.errors.city.message}</p>
                          )}
                        </div>

                        <div className="space-y-2">
                          <label htmlFor="uf" className="block text-sm font-medium text-gray-700">
                            UF <span className="text-red-500">*</span>
                          </label>
                          <select
                            {...methods.register('uf')}
                            id="uf"
                            className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                          >
                            <option value="">Selecione a UF</option>
                            <option value="AC">AC</option>
                            <option value="AL">AL</option>
                            <option value="AP">AP</option>
                            <option value="AM">AM</option>
                            <option value="BA">BA</option>
                            <option value="CE">CE</option>
                            <option value="DF">DF</option>
                            <option value="ES">ES</option>
                            <option value="GO">GO</option>
                            <option value="MA">MA</option>
                            <option value="MT">MT</option>
                            <option value="MS">MS</option>
                            <option value="MG">MG</option>
                            <option value="PA">PA</option>
                            <option value="PB">PB</option>
                            <option value="PR">PR</option>
                            <option value="PE">PE</option>
                            <option value="PI">PI</option>
                            <option value="RJ">RJ</option>
                            <option value="RN">RN</option>
                            <option value="RS">RS</option>
                            <option value="RO">RO</option>
                            <option value="RR">RR</option>
                            <option value="SC">SC</option>
                            <option value="SP">SP</option>
                            <option value="SE">SE</option>
                            <option value="TO">TO</option>
                          </select>
                          {methods.formState.errors.uf && (
                            <p className="text-sm text-red-600">{methods.formState.errors.uf.message}</p>
                          )}
                        </div>
                      </div>
                    </div>
                  </div>

                  {/* Segurança */}
                  <div className="space-y-4">
                    <h4 className="text-lg font-semibold text-gray-800 border-b border-gray-200 pb-2">
                      Segurança
                    </h4>
                    
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                          Senha <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('password')}
                          id="password"
                          type="password"
                          placeholder="Mínimo 8 caracteres"
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                        />
                        {methods.formState.errors.password && (
                          <p className="text-sm text-red-600">{methods.formState.errors.password.message}</p>
                        )}
                      </div>

                      <div className="space-y-2">
                        <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700">
                          Confirmar Senha <span className="text-red-500">*</span>
                        </label>
                        <input
                          {...methods.register('confirmPassword')}
                          id="confirmPassword"
                          type="password"
                          placeholder="Confirme sua senha"
                          className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                        />
                        {methods.formState.errors.confirmPassword && (
                          <p className="text-sm text-red-600">{methods.formState.errors.confirmPassword.message}</p>
                        )}
                      </div>
                    </div>

                    <div className="space-y-4">
                      <div className="flex items-center space-x-3 p-4 bg-red-50 rounded-xl border border-red-200">
                        <input
                          {...methods.register('enableTOTP')}
                          id="enableTOTP"
                          type="checkbox"
                          checked={true}
                          disabled={true}
                          readOnly={true}
                          className="w-5 h-5 text-red-500 bg-gray-100 border-gray-300 rounded focus:ring-red-500 focus:ring-2"
                        />
                        <label htmlFor="enableTOTP" className="text-sm font-medium text-gray-700">
                          Autenticador (TOTP) obrigatório
                        </label>
                      </div>

                      <div className="flex items-center space-x-3 p-4 bg-red-50 rounded-xl border border-red-200">
                        <input
                          {...methods.register('acceptTerms')}
                          id="acceptTerms"
                          type="checkbox"
                          className="w-5 h-5 text-red-500 bg-gray-100 border-gray-300 rounded focus:ring-red-500 focus:ring-2"
                        />
                        <label htmlFor="acceptTerms" className="text-sm font-medium text-gray-700">
                          Eu aceito os{' '}
                          <button
                            type="button"
                            onClick={() => setShowTermos(true)}
                            className="text-red-600 hover:text-red-800 underline"
                          >
                            Termos de Uso
                          </button>
                        {' '}e a{' '}
                          <button
                            type="button"
                            onClick={() => setShowPrivacidade(true)}
                            className="text-red-600 hover:text-red-800 underline"
                          >
                            Política de Privacidade
                          </button>
                          <span className="text-red-500">*</span>
                        </label>
                      </div>
                      {methods.formState.errors.acceptTerms && (
                        <p className="text-sm text-red-600">{methods.formState.errors.acceptTerms.message}</p>
                      )}
                    </div>
                  </div>

                  <button
                    type="submit"
                    className="w-full bg-gradient-to-r from-red-600 to-red-700 text-white py-3 px-6 rounded-lg font-semibold hover:shadow-lg hover:scale-105 transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
                  >
                    Criar Conta
                  </button>
                </form>
              </FormProvider>
            </div>

            {/* Footer */}
            <div className="px-8 py-6 border-t border-gray-200">
              <div className="flex items-center justify-between text-sm text-gray-500">
                <span>© 2024 FlightPlan. Todos os direitos reservados.</span>
                <div className="flex space-x-4">
                  <button type="button" className="hover:text-red-600 bg-transparent border-none p-0">Suporte</button>
                  <span>|</span>
                  <button type="button" className="hover:text-red-600 bg-transparent border-none p-0">Contato</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* TOTP Setup */}
      {step === 'totp' && (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
          <div className="max-w-md w-full bg-white rounded-2xl shadow-soft-lg p-8">
            <div className="text-center mb-8">
              <div className="w-16 h-16 bg-gradient-to-r from-red-600 to-red-700 rounded-2xl flex items-center justify-center mx-auto mb-4">
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
                className="w-full bg-gradient-to-r from-red-600 to-red-700 text-white py-3 px-6 rounded-xl font-semibold hover:shadow-lg hover:scale-105 transition-all duration-300" 
                onClick={onVerify}
              >
                Confirmar 2FA
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Done */}
      {step === 'done' && (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-gray-50 to-gray-100 p-4">
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
              className="inline-flex items-center px-6 py-3 bg-gradient-to-r from-red-600 to-red-700 text-white rounded-xl font-semibold hover:shadow-lg hover:scale-105 transition-all duration-300"
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
