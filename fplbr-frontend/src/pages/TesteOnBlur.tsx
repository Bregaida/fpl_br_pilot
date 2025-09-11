import { useForm, FormProvider } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import FormField from '../components/FormField'

const schema = z.object({
  nome: z.string().min(1, 'Nome é obrigatório'),
  email: z.string().min(1, 'Email é obrigatório').email('Email inválido'),
  idade: z.string().min(1, 'Idade é obrigatória')
})

type FormData = z.infer<typeof schema>

export default function TesteOnBlurPage() {
  const methods = useForm<FormData>({
    resolver: zodResolver(schema),
    mode: 'onBlur', // Validação apenas quando sai do campo
    defaultValues: {
      nome: '',
      email: '',
      idade: ''
    }
  })

  const { handleSubmit, formState: { errors, touchedFields, dirtyFields } } = methods

  const onSubmit = (data: FormData) => {
    console.log('Dados:', data)
    alert('Formulário válido!')
  }

  return (
    <div className="p-8 max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">Teste OnBlur Simples</h1>
      
      <div className="mb-4 p-4 bg-yellow-50 border border-yellow-200 rounded">
        <h2 className="font-semibold text-yellow-800">Teste:</h2>
        <ul className="text-sm text-yellow-700 mt-2 space-y-1">
          <li>• Clique em um campo e deixe em branco</li>
          <li>• Saia do campo (Tab ou clique fora)</li>
          <li>• A mensagem de erro deve aparecer</li>
        </ul>
      </div>

      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4">
          <FormField name="nome" label="Nome" required />
          <FormField name="email" label="Email" type="email" required />
          <FormField name="idade" label="Idade" required />
          
          <button 
            type="submit" 
            className="px-6 py-3 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition-colors"
          >
            Testar
          </button>
        </form>
      </FormProvider>
      
      <div className="mt-8 p-4 bg-gray-50 rounded">
        <h3 className="font-semibold mb-2">Debug Info:</h3>
        <div className="text-xs space-y-2">
          <div>
            <strong>Errors:</strong>
            <pre>{JSON.stringify(errors, null, 2)}</pre>
          </div>
          <div>
            <strong>Touched Fields:</strong>
            <pre>{JSON.stringify(touchedFields, null, 2)}</pre>
          </div>
          <div>
            <strong>Dirty Fields:</strong>
            <pre>{JSON.stringify(dirtyFields, null, 2)}</pre>
          </div>
        </div>
      </div>
    </div>
  )
}
