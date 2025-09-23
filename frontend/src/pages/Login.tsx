import React from 'react'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Link } from 'react-router-dom'
import { AuthAPI } from '../services/auth'
import TotpInput from '../components/TotpInput'
import FormField from '../components/FormField'
import { loginSchema, LoginFormData } from '../schemas/validation'

export default function LoginPage() {
  const [totp, setTotp] = React.useState('')
  const [needTotp, setNeedTotp] = React.useState(false)
  const [msg, setMsg] = React.useState('')

  const methods = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    mode: 'onBlur',
    reValidateMode: 'onBlur',
    defaultValues: {
      login: '',
      password: '',
      totp: ''
    }
  })

  const { handleSubmit, watch } = methods

  async function onSubmit(data: LoginFormData) {
    setMsg('')
    try {
      const { data: response } = await AuthAPI.login(
        data.login, 
        data.password, 
        needTotp ? parseInt(totp || '0', 10) : undefined
      )
      if (response?.ok) { 
        setMsg('Autenticado')
        return 
      }
      if (response?.reason === 'totp_requerido') { 
        setNeedTotp(true)
        setMsg('Informe o código do Authenticator')
        return 
      }
      if (response?.reason) setMsg(response.reason)
    } catch (e: any) {
      setMsg(e?.response?.data?.reason || 'Falha no login')
    }
  }

  async function onTotpOnly() {
    setNeedTotp(true)
    setMsg('Digite o código do seu Authenticator')
  }
  
  async function onTotpOnlySubmit() {
    try {
      const code = parseInt(totp || '0', 10)
      const { data } = await AuthAPI.loginTotpOnly(watch('login'), code)
      if (data?.ok) setMsg('Sessão temporária gerada. Vá para Troca de Senha.')
    } catch (error: any) { 
      const errorMsg = error?.response?.data?.error || error?.message || 'TOTP inválido'
      setMsg(errorMsg)
    }
  }

  return (
    <section className="p-4 grid gap-4">
      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)} className="grid gap-3">
          <FormField name="login" label="Email ou CPF" placeholder="Digite seu email ou CPF" required />
          {!needTotp && <FormField name="password" label="Senha" type="password" placeholder="Digite sua senha" required />}
          {needTotp && <TotpInput value={totp} onChange={setTotp} />}
          {!needTotp && (
            <button type="submit" className="px-4 py-2 rounded bg-indigo-600 text-white">
              Entrar
            </button>
          )}
          {needTotp && (
            <button type="button" onClick={onTotpOnlySubmit} className="px-4 py-2 rounded bg-indigo-600 text-white">
              Entrar com Authenticator
            </button>
          )}
        </form>
      </FormProvider>
      <div className="flex items-center gap-4 text-sm">
        {!needTotp && (
          <button className="underline" onClick={onTotpOnly}>
            Entrar com Authenticator (sem senha)
          </button>
        )}
        {needTotp && (
          <button className="underline" onClick={() => setNeedTotp(false)}>
            Voltar ao login normal
          </button>
        )}
        <Link className="underline" to="/esqueci-senha">
          Esqueci minha senha
        </Link>
      </div>
      {msg && <div className="text-sm text-slate-700">{msg}</div>}
    </section>
  )
}


