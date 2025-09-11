import { z } from 'zod'

// Validação de CPF
const cpfSchema = z.string()
  .min(1, 'CPF é obrigatório')
  .refine((cpf: string) => {
    const cleanCpf = cpf.replace(/\D/g, '')
    return cleanCpf.length === 11
  }, 'CPF deve ter 11 dígitos')
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
  }, 'CPF inválido')

// Validação de email
const emailSchema = z.string()
  .min(1, 'Email é obrigatório')
  .email('Email inválido')

// Validação de senha
const passwordSchema = z.string()
  .min(8, 'Senha deve ter pelo menos 8 caracteres')
  .refine((password: string) => (password.match(/\d/g) || []).length >= 2, 'Senha deve ter pelo menos 2 números')
  .refine((password: string) => /[^\w\s]/.test(password), 'Senha deve ter pelo menos 1 caractere especial')
  .refine((password: string) => /[A-Z]/.test(password), 'Senha deve ter pelo menos 1 letra maiúscula')
  .refine((password: string) => /[a-z]/.test(password), 'Senha deve ter pelo menos 1 letra minúscula')

// Validação de telefone
const phoneSchema = z.string()
  .min(1, 'Telefone é obrigatório')
  .refine((phone: string) => {
    const cleanPhone = phone.replace(/\D/g, '')
    return cleanPhone.length >= 10 && cleanPhone.length <= 11
  }, 'Telefone deve ter 10 ou 11 dígitos')

// Validação de CEP
const cepSchema = z.string()
  .min(1, 'CEP é obrigatório')
  .refine((cep: string) => {
    const cleanCep = cep.replace(/\D/g, '')
    return cleanCep.length === 8
  }, 'CEP deve ter 8 dígitos')

// Schema de cadastro
export const cadastroSchema = z.object({
  fullName: z.string().min(1, 'Nome completo é obrigatório'),
  email: emailSchema,
  cpf: cpfSchema,
  telefone: phoneSchema,
  cep: cepSchema,
  address: z.string().min(1, 'Endereço é obrigatório'),
  addressNumber: z.string().min(1, 'Número é obrigatório'),
  addressComplement: z.string().optional(),
  neighborhood: z.string().min(1, 'Bairro é obrigatório'),
  city: z.string().min(1, 'Cidade é obrigatória'),
  uf: z.string().min(2, 'UF é obrigatória').max(2, 'UF deve ter 2 caracteres'),
  birthDate: z.string().min(1, 'Data de nascimento é obrigatória'),
  maritalStatus: z.string().min(1, 'Estado civil é obrigatório').refine((val: string) => val !== 'Selecione', 'Selecione um estado civil'),
  pilotType: z.string().min(1, 'Tipo de piloto é obrigatório').refine((val: string) => val !== 'Selecione', 'Selecione um tipo de piloto'),
  company: z.string().optional(),
  loginAlias: z.string().min(1, 'Login é obrigatório'),
  password: passwordSchema,
  confirmPassword: z.string().min(1, 'Confirmação de senha é obrigatória')
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
