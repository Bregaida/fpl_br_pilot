import React from 'react'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AuthAPI } from '../services/auth'
import FormField from '../components/FormField'
import PasswordField from '../components/PasswordField'
import ConfirmPasswordField from '../components/ConfirmPasswordField'
import { trocaSenhaSchema, TrocaSenhaFormData } from '../schemas/validation'

export default function TrocaSenhaPage() {
  const [msg, setMsg] = React.useState('')

  const methods = useForm<TrocaSenhaFormData>({
    resolver: zodResolver(trocaSenhaSchema),
    mode: 'onBlur',
    reValidateMode: 'onBlur',
    defaultValues: {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  })

  const { handleSubmit } = methods

  async function onSubmit(data: TrocaSenhaFormData) {
    setMsg('')
    try {
      const { data: response } = await AuthAPI.reset(data.currentPassword, data.newPassword)
      setMsg(response?.ok ? 'Senha alterada com sucesso' : 'Falha na alteração da senha')
    } catch (error: any) {
      setMsg(error?.response?.data?.reason || 'Falha na troca de senha')
    }
  }

  return (
    <section className="p-4 grid gap-3">
      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)} className="grid gap-2">
          <FormField name="currentPassword" label="Senha Atual" type="password" placeholder="Digite sua senha atual" required />
          <PasswordField name="newPassword" label="Nova Senha" placeholder="Digite sua nova senha" required />
          <ConfirmPasswordField 
            name="confirmPassword" 
            label="Confirme a Nova Senha" 
            passwordFieldName="newPassword"
            placeholder="Confirme sua nova senha"
            required 
          />
          <button type="submit" className="px-4 py-2 rounded bg-indigo-600 text-white">
            Salvar
          </button>
        </form>
      </FormProvider>
      {msg && <div className="text-sm text-slate-700">{msg}</div>}
    </section>
  )
}


