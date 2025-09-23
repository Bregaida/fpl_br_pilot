import React from 'react'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useNavigate } from 'react-router-dom'
import { apiClient } from '../services/api'

// Schema de validação (sem nome, pois vem do usuário logado)
const pilotoSchema = z.object({
  codigoAnac: z.string()
    .min(1, 'Código ANAC é obrigatório')
    .max(20, 'Código ANAC deve ter no máximo 20 caracteres')
    .regex(/^[0-9]+$/, 'Código ANAC deve conter apenas números'),
  nomePista: z.string()
    .min(1, 'Nome de pista é obrigatório')
    .max(50, 'Nome de pista deve ter no máximo 50 caracteres')
})

type PilotoFormData = z.infer<typeof pilotoSchema>

interface Piloto {
  id?: number
  nome: string
  codigoAnac: string
  nomePista: string
}

export default function CadastroPilotoPage() {
  const navigate = useNavigate()
  const [loading, setLoading] = React.useState(false)
  const [msg, setMsg] = React.useState('')
  const [msgType, setMsgType] = React.useState<'success' | 'error'>('error')
  const [pilotoExistente, setPilotoExistente] = React.useState<Piloto | null>(null)
  const [isEditMode, setIsEditMode] = React.useState(false)
  const [usuarioLogado, setUsuarioLogado] = React.useState<{nomeCompleto: string} | null>(null)

  const methods = useForm<PilotoFormData>({
    resolver: zodResolver(pilotoSchema),
    mode: 'onBlur',
    reValidateMode: 'onBlur',
    defaultValues: {
      codigoAnac: '',
      nomePista: ''
    }
  })

  const { handleSubmit, setValue, watch } = methods

  // Buscar usuário logado e verificar piloto existente
  React.useEffect(() => {
    buscarUsuarioLogado()
    verificarPilotoExistente()
  }, [])

  async function buscarUsuarioLogado() {
    try {
      const usuario = await apiClient.getCurrentUser()
      setUsuarioLogado(usuario)
    } catch (error) {
      console.error('Erro ao buscar usuário logado:', error)
      setMsg('Erro ao carregar dados do usuário')
      setMsgType('error')
    }
  }

  async function verificarPilotoExistente() {
    try {
      const response = await fetch('/api/v1/pilotos')
      const data = await response.json()
      
      if (data.success && data.data && data.data.length > 0) {
        const piloto = data.data[0] // Pega o primeiro (e único) piloto
        setPilotoExistente(piloto)
        setIsEditMode(true)
        
        // Preencher o formulário com os dados existentes
        setValue('codigoAnac', piloto.codigoAnac)
        setValue('nomePista', piloto.nomePista)
      }
    } catch (error) {
      console.error('Erro ao verificar piloto existente:', error)
    }
  }

  async function onSubmit(data: PilotoFormData) {
    setLoading(true)
    setMsg('')

    try {
      const url = isEditMode && pilotoExistente?.id 
        ? `/api/v1/pilotos/${pilotoExistente.id}`
        : '/api/v1/pilotos'
      
      const method = isEditMode ? 'PUT' : 'POST'
      
      // Incluir o nome do usuário logado nos dados
      const dadosCompletos = {
        ...data,
        nome: usuarioLogado?.nomeCompleto || ''
      }
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(dadosCompletos)
      })

      const result = await response.json()

      if (result.success) {
        setMsg(isEditMode ? 'Piloto atualizado com sucesso!' : 'Piloto cadastrado com sucesso!')
        setMsgType('success')
        
        // Atualizar dados locais
        if (result.data) {
          setPilotoExistente(result.data)
        }
        
        // Redirecionar após 2 segundos
        setTimeout(() => {
          navigate('/')
        }, 2000)
      } else {
        setMsg(result.message || 'Erro ao salvar piloto')
        setMsgType('error')
      }
    } catch (error) {
      console.error('Erro ao salvar piloto:', error)
      setMsg('Erro de conexão. Tente novamente.')
      setMsgType('error')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="grid gap-6 p-4">
      <header className="space-y-1">
        <h1 className="text-3xl font-semibold tracking-tight">
          {isEditMode ? 'Editar Piloto' : 'Cadastro de Piloto'}
        </h1>
        <p className="text-slate-600">
          {isEditMode 
            ? 'Atualize os dados do piloto cadastrado' 
            : 'Preencha os dados do piloto para uso nos planos de voo'
          }
        </p>
      </header>

      {msg && (
        <div className={`p-3 rounded border ${
          msgType === 'success' 
            ? 'border-green-300 bg-green-50 text-green-800' 
            : 'border-red-300 bg-red-50 text-red-800'
        }`}>
          <div className="flex items-center gap-2">
            {msgType === 'success' ? (
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
              </svg>
            ) : (
              <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
              </svg>
            )}
            <span className="font-medium">{msg}</span>
          </div>
        </div>
      )}

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)} className="grid gap-6">
          {/* Dados do Piloto */}
          <div className="grid gap-4 p-4 rounded-2xl bg-white border border-slate-200 shadow-sm">
            <h2 className="text-lg font-semibold" id="bloco-dados-piloto">Dados do Piloto</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {/* Nome do Piloto (não editável) */}
              <div className="grid gap-1">
                <label className="font-medium">Nome do Piloto</label>
                <div className="input bg-gray-50 text-gray-700 cursor-not-allowed">
                  {usuarioLogado?.nomeCompleto || 'Carregando...'}
                </div>
                <p className="text-sm text-slate-500">
                  Nome obtido do seu cadastro de usuário
                </p>
              </div>

              {/* Código ANAC */}
              <div className="grid gap-1">
                <label className="font-medium">Código ANAC/CANAC *</label>
                <input 
                  {...methods.register('codigoAnac')}
                  className="input" 
                  placeholder="177798"
                />
                <p className="text-sm text-slate-500">
                  Digite apenas números (exemplo: 177798)
                </p>
                {methods.formState.errors.codigoAnac && (
                  <p className="text-red-600 text-sm">{methods.formState.errors.codigoAnac.message}</p>
                )}
              </div>

              {/* Nome de Pista */}
              <div className="grid gap-1 md:col-span-2">
                <label className="font-medium">Nome de Pista *</label>
                <input 
                  {...methods.register('nomePista')}
                  className="input" 
                  placeholder="Digite o nome de pista do piloto"
                />
                <p className="text-sm text-slate-500">
                  Nome que aparecerá nos planos de voo
                </p>
                {methods.formState.errors.nomePista && (
                  <p className="text-red-600 text-sm">{methods.formState.errors.nomePista.message}</p>
                )}
              </div>
            </div>
          </div>

          {/* Botões de Ação */}
          <div className="flex gap-4 justify-end">
            <button
              type="button"
              onClick={() => navigate('/')}
              className="btn btn-ghost"
            >
              Cancelar
            </button>
            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary"
            >
              {loading ? (
                <>
                  <svg className="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Salvando...
                </>
              ) : (
                <>
                  <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  {isEditMode ? 'Atualizar Piloto' : 'Salvar Piloto'}
                </>
              )}
            </button>
          </div>
        </form>
      </FormProvider>
    </section>
  )
}
