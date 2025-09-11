import React from 'react'
import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AuthAPI, CepAPI } from '../services/auth'
import QrCode from '../components/QrCode'
import TotpInput from '../components/TotpInput'
import FormField from '../components/FormField'
import PasswordField from '../components/PasswordField'
import ConfirmPasswordField from '../components/ConfirmPasswordField'
import SelectField from '../components/SelectField'
import { cadastroSchema, CadastroFormData } from '../schemas/validation'

export default function CadastroPage() {
  const [step, setStep] = React.useState<'form'|'totp'|'done'>('form')
  const [otpauth, setOtpauth] = React.useState<string>('')
  const [secretMasked, setSecretMasked] = React.useState<string>('')
  const [totp, setTotp] = React.useState<string>('')
  const [msg, setMsg] = React.useState<string>('')

  const methods = useForm<CadastroFormData>({
    resolver: zodResolver(cadastroSchema),
    mode: 'onBlur', // Validação apenas quando sai do campo
    reValidateMode: 'onBlur', // Revalidação quando sai do campo
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
      try {
        const { data } = await CepAPI.lookup(cep)
        setValue('address', data.endereco)
        setValue('neighborhood', data.bairro)
        setValue('city', data.cidade)
        setValue('uf', data.uf)
      } catch {}
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

  const maritalStatusOptions = [
    { value: 'Solteiro(a)', label: 'Solteiro(a)' },
    { value: 'Casado(a)', label: 'Casado(a)' },
    { value: 'Divorciado(a)', label: 'Divorciado(a)' },
    { value: 'Viúvo(a)', label: 'Viúvo(a)' }
  ]

  const pilotTypeOptions = [
    { value: 'Piloto Aluno', label: 'Piloto Aluno' },
    { value: 'Piloto Comercial', label: 'Piloto Comercial' },
    { value: 'Piloto Privado', label: 'Piloto Privado' },
    { value: 'Piloto Desportivo', label: 'Piloto Desportivo' },
    { value: 'Piloto de Plataforma Virtual', label: 'Piloto de Plataforma Virtual' },
    { value: 'Piloto de Drone', label: 'Piloto de Drone' }
  ]

  return (
    <section className="p-4 grid gap-6">
      {step === 'form' && (
        <FormProvider {...methods}>
          <form onSubmit={handleSubmit(onSubmit)} className="grid gap-3">
            <FormField name="fullName" label="Nome Completo" required />
            <FormField name="email" label="Email" type="email" required />
            <FormField name="cpf" label="CPF" placeholder="000.000.000-00" required />
            <FormField name="telefone" label="Telefone" placeholder="(11) 9xxxx-xxxx" required />
            <FormField name="cep" label="CEP" placeholder="00000-000" required onBlur={onCepBlur} />
            <FormField name="address" label="Endereço" required />
            <FormField name="addressNumber" label="Número" required />
            <FormField name="addressComplement" label="Complemento" />
            <FormField name="neighborhood" label="Bairro" required />
            <FormField name="city" label="Cidade" required />
            <FormField name="uf" label="UF" required />
            <FormField name="birthDate" label="Data de Nascimento" type="date" required />
            <SelectField 
              name="maritalStatus" 
              label="Estado Civil" 
              options={maritalStatusOptions}
              required 
            />
            <SelectField 
              name="pilotType" 
              label="Eu sou" 
              options={pilotTypeOptions}
              required 
            />
            <FormField name="company" label="Instituição/Empresa" />
            <FormField name="loginAlias" label="Login (email ou CPF)" required />
            <PasswordField name="password" label="Senha" required />
            <ConfirmPasswordField 
              name="confirmPassword" 
              label="Repita a Senha" 
              passwordFieldName="password"
              required 
            />
            {msg && <div className="text-red-700 text-sm">{msg}</div>}
            <button type="submit" className="px-4 py-2 rounded bg-indigo-600 text-white">
              Continuar
            </button>
          </form>
        </FormProvider>
      )}

      {step==='totp' && (
        <div className="grid gap-4">
          <QrCode otpauthUrl={otpauth} />
          <div className="text-sm text-slate-600">Secret: {secretMasked}</div>
          <TotpInput value={totp} onChange={setTotp} />
          <button className="px-4 py-2 rounded bg-emerald-600 text-white" onClick={onVerify}>Confirmar 2FA</button>
        </div>
      )}

      {step==='done' && (
        <div className="p-4 bg-emerald-50 border border-emerald-200 rounded">Cadastro concluído! Vá para login.</div>
      )}
    </section>
  )
}


