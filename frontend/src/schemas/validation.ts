import { z } from 'zod'

// Validação de CPF
const cpfSchema = z.string()
  .min(1, 'O CPF é obrigatório')
  .refine((cpf: string) => {
    const cleanCpf = cpf.replace(/\D/g, '')
    return cleanCpf.length === 11
  }, 'Campo CPF inválido')
  .refine((cpf: string) => {
    const cleanCpf = cpf.replace(/\D/g, '')
    // Validação básica de CPF
    if (/^(\d)\1{10}$/.test(cleanCpf)) return false
    
    let sum = 0
    for (let i = 0; i < 9; i++) {
      sum += parseInt(cleanCpf.charAt(i)) * (10 - i)
    }
    let remainder = 11 - (sum % 11)
    if (remainder === 10 || remainder === 11) remainder = 0
    if (remainder !== parseInt(cleanCpf.charAt(9))) return false
    
    sum = 0
    for (let i = 0; i < 10; i++) {
      sum += parseInt(cleanCpf.charAt(i)) * (11 - i)
    }
    remainder = 11 - (sum % 11)
    if (remainder === 10 || remainder === 11) remainder = 0
    return remainder === parseInt(cleanCpf.charAt(10))
  }, 'Campo CPF inválido')

// Validação de email
const emailSchema = z.string()
  .min(1, 'O E-mail é obrigatório')
  .email('Campo E-mail inválido')

// Validação de senha
const passwordSchema = z.string()
  .min(8, 'Senha deve ter pelo menos 8 caracteres')
  .refine((password: string) => (password.match(/\d/g) || []).length >= 2, 'Senha deve ter pelo menos 2 números')
  .refine((password: string) => /[^\w\s]/.test(password), 'Senha deve ter pelo menos 1 caractere especial')
  .refine((password: string) => /[A-Z]/.test(password), 'Senha deve ter pelo menos 1 letra maiúscula')
  .refine((password: string) => /[a-z]/.test(password), 'Senha deve ter pelo menos 1 letra minúscula')

// Validação de telefone brasileiro
const phoneSchema = z.string()
  .min(1, 'O Telefone é obrigatório')
  .refine((phone: string) => {
    const cleanPhone = phone.replace(/\D/g, '')
    // Deve ter 10 ou 11 dígitos
    if (cleanPhone.length < 10 || cleanPhone.length > 11) return false
    // DDD não pode ser 0
    if (cleanPhone.startsWith('0')) return false
    // DDD deve estar entre 11 e 99
    const ddd = parseInt(cleanPhone.substring(0, 2))
    if (ddd < 11 || ddd > 99) return false
    return true
  }, 'Telefone inválido')

// Validação de CEP
const cepSchema = z.string()
  .min(1, 'CEP é campo obrigatório')
  .refine((cep: string) => {
    const cleanCep = cep.replace(/\D/g, '')
    return cleanCep.length === 8
  }, 'CEP deve ter 8 dígitos')

// Validação de data de nascimento
const birthDateSchema = z.string()
  .min(1, 'A data de nascimento é obrigatória')
  .refine((date: string) => {
    // Verifica formato dd/mm/yyyy
    const dateRegex = /^(\d{2})\/(\d{2})\/(\d{4})$/
    if (!dateRegex.test(date)) return false
    
    const match = dateRegex.exec(date)
    if (!match) return false
    const [, day, month, year] = match
    const birthDate = new Date(parseInt(year), parseInt(month) - 1, parseInt(day))
    const today = new Date()
    const age = today.getFullYear() - birthDate.getFullYear()
    
    // Verifica se a data é válida
    if (birthDate.getDate() !== parseInt(day) || 
        birthDate.getMonth() !== parseInt(month) - 1 || 
        birthDate.getFullYear() !== parseInt(year)) {
      return false
    }
    
    // Verifica idade mínima (10 anos) e máxima (1900)
    return age >= 10 && age <= 124 && parseInt(year) >= 1900
  }, 'Data inválida')

// Validação de UF
const ufSchema = z.string()
  .min(1, 'UF é campo obrigatório')
  .refine((uf: string) => {
    const validUFs = [
      'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA',
      'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN',
      'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
    ]
    return validUFs.includes(uf.toUpperCase())
  }, 'UF inválida')

// Validação de nome completo
const fullNameSchema = z.string()
  .min(1, 'O Nome Completo é obrigatório')
  .refine((name: string) => {
    const trimmedName = name.trim()
    // Deve ter pelo menos 2 palavras (nome e sobrenome)
    const words = trimmedName.split(/\s+/)
    if (words.length < 2) return false
    
    // Cada palavra deve começar com maiúscula
    return words.every(word => /^[A-ZÁÀÂÃÉÊÍÓÔÕÚÇ][a-záàâãéêíóôõúç]*$/.test(word))
  }, 'Campo inválido, nome incompleto')

// Schema de cadastro
export const cadastroSchema = z.object({
  fullName: fullNameSchema,
  email: emailSchema,
  cpf: cpfSchema,
  telefone: phoneSchema,
  cep: cepSchema,
  address: z.string().min(1, 'Logradouro é campo obrigatório'),
  addressNumber: z.string().optional(), // Número é opcional
  addressComplement: z.string().optional(),
  neighborhood: z.string().min(1, 'Bairro é campo obrigatório'),
  city: z.string().min(1, 'Cidade é campo obrigatório'),
  uf: ufSchema,
  birthDate: birthDateSchema,
  maritalStatus: z.string().optional(),
  pilotType: z.string().min(1, 'Tipo de Licença é obrigatório'),
  company: z.string().min(1, 'Empresa/Escola é obrigatória'),
  password: passwordSchema,
  confirmPassword: z.string().min(1, 'Confirmação de senha é obrigatória'),
  enableTOTP: z.boolean().refine((val) => val === true, 'Autenticação de Dois Fatores (TOTP) é campo obrigatório'),
  acceptTerms: z.boolean().refine((val) => val === true, 'Eu aceito os Termos de Uso e a Política de Privacidade é campo obrigatório')
}).refine((data: any) => data.password === data.confirmPassword, {
  message: 'Senhas não conferem',
  path: ['confirmPassword']
})

// Schema de login
export const loginSchema = z.object({
  login: z.string().min(1, 'Email ou CPF é obrigatório'),
  password: z.string().min(1, 'Senha é obrigatória'),
  totp: z.string().optional()
})

// Schema de esqueci senha
export const esqueciSenhaSchema = z.object({
  login: z.string().min(1, 'Email ou CPF é obrigatório')
})

// Schema de troca de senha
export const trocaSenhaSchema = z.object({
  currentPassword: z.string().min(1, 'Senha atual é obrigatória'),
  newPassword: passwordSchema,
  confirmPassword: z.string().min(1, 'Confirmação de senha é obrigatória')
}).refine((data: any) => data.newPassword === data.confirmPassword, {
  message: 'Senhas não conferem',
  path: ['confirmPassword']
})

export type CadastroFormData = z.infer<typeof cadastroSchema>
export type LoginFormData = z.infer<typeof loginSchema>
export type EsqueciSenhaFormData = z.infer<typeof esqueciSenhaSchema>
export type TrocaSenhaFormData = z.infer<typeof trocaSenhaSchema>
