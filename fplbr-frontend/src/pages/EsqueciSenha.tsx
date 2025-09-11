import React from 'react'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AuthAPI } from '../services/auth'
import FormField from '../components/FormField'
import { esqueciSenhaSchema, EsqueciSenhaFormData } from '../schemas/validation'

export default function EsqueciSenhaPage() {
  const [resp, setResp] = React.useState<any>(null)

  const methods = useForm<EsqueciSenhaFormData>({
    resolver: zodResolver(esqueciSenhaSchema),
    mode: 'onBlur',
    reValidateMode: 'onBlur',
    defaultValues: {
      login: ''
    }
  })

  const { handleSubmit } = methods

  async function onSubmit(data: EsqueciSenhaFormData) {
    try {
      const { data: response } = await AuthAPI.forgot(data.login)
      setResp(response)
    } catch (error: any) {
      setResp({ error: error?.response?.data?.reason || 'Erro ao enviar solicitação' })
    }
  }

  return (
    <section className="p-4 grid gap-3">
      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)} className="grid gap-2">
          <FormField name="login" label="Email ou CPF" placeholder="Digite seu email ou CPF" required />
          <button type="submit" className="px-4 py-2 rounded bg-indigo-600 text-white">
            Enviar
          </button>
        </form>
      </FormProvider>
      {resp && (
        <pre className="bg-gray-50 p-3 rounded text-sm">
          {JSON.stringify(resp, null, 2)}
        </pre>
      )}
    </section>
  )
}


