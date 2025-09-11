import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import FormField from '../components/FormField'
import PasswordField from '../components/PasswordField'
import ConfirmPasswordField from '../components/ConfirmPasswordField'
import SelectField from '../components/SelectField'
import { cadastroSchema, CadastroFormData } from '../schemas/validation'

export default function TesteValidacaoPage() {
  const methods = useForm<CadastroFormData>({
    resolver: zodResolver(cadastroSchema),
    mode: 'onChange',
    reValidateMode: 'onBlur',
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

  const { handleSubmit, formState: { errors } } = methods

  const onSubmit = (data: CadastroFormData) => {
    console.log('Dados do formulário:', data)
    alert('Formulário válido!')
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
    <div className="p-8 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Teste de Validação em Tempo Real</h1>
      
      <div className="mb-4 p-4 bg-blue-50 border border-blue-200 rounded">
        <h2 className="font-semibold text-blue-800">Instruções:</h2>
        <ul className="text-sm text-blue-700 mt-2 space-y-1">
          <li>• Clique em um campo e deixe em branco</li>
          <li>• Saia do campo (clique fora ou pressione Tab)</li>
          <li>• A mensagem de erro deve aparecer abaixo do campo</li>
          <li>• Digite algo válido e a mensagem deve desaparecer</li>
        </ul>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="fullName" label="Nome Completo" required />
            <FormField name="email" label="Email" type="email" required />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="cpf" label="CPF" placeholder="000.000.000-00" required />
            <FormField name="telefone" label="Telefone" placeholder="(11) 9xxxx-xxxx" required />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="cep" label="CEP" placeholder="00000-000" required />
            <FormField name="address" label="Endereço" required />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="addressNumber" label="Número" required />
            <FormField name="neighborhood" label="Bairro" required />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="city" label="Cidade" required />
            <FormField name="uf" label="UF" required />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="birthDate" label="Data de Nascimento" type="date" required />
            <SelectField 
              name="maritalStatus" 
              label="Estado Civil" 
              options={maritalStatusOptions}
              required 
            />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <SelectField 
              name="pilotType" 
              label="Eu sou" 
              options={pilotTypeOptions}
              required 
            />
            <FormField name="company" label="Instituição/Empresa" />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField name="loginAlias" label="Login (email ou CPF)" required />
            <PasswordField name="password" label="Senha" required />
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <ConfirmPasswordField 
              name="confirmPassword" 
              label="Repita a Senha" 
              passwordFieldName="password"
              required 
            />
            <div></div>
          </div>
          
          <div className="mt-6">
            <button 
              type="submit" 
              className="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
            >
              Testar Validação
            </button>
          </div>
        </form>
      </FormProvider>
      
      <div className="mt-8 p-4 bg-gray-50 rounded">
        <h3 className="font-semibold mb-2">Estado do Formulário:</h3>
        <pre className="text-xs overflow-auto">
          {JSON.stringify(errors, null, 2)}
        </pre>
      </div>
    </div>
  )
}
