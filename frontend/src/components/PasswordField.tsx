import { useFormContext } from 'react-hook-form'
import { useState } from 'react'

interface PasswordFieldProps {
  readonly name: string
  readonly placeholder?: string
  readonly showValidation?: boolean
  readonly icon?: React.ReactNode
}

export default function PasswordField({ 
  name, 
  placeholder = 'Ex: MinhaSenh@123',
  showValidation = true,
  icon
}: PasswordFieldProps) {
  const { register, watch, formState: { errors, touchedFields, dirtyFields } } = useFormContext()
  const [showPassword, setShowPassword] = useState(false)
  const hasError = errors[name]
  const isTouched = touchedFields[name] || dirtyFields[name]
  const password = watch(name) || ''

  const checks = {
    len: password.length >= 8,
    digits: (password.match(/\d/g) || []).length >= 2,
    special: /[^\w\s]/.test(password),
    upper: /[A-Z]/.test(password),
    lower: /[a-z]/.test(password),
  }

  const strength = Object.values(checks).filter(Boolean).length
  
  const getStrengthColor = () => {
    if (strength <= 2) return 'red'
    if (strength <= 4) return 'yellow'
    return 'green'
  }
  
  const getStrengthText = () => {
    if (strength <= 2) return 'Fraca'
    if (strength <= 4) return 'Média'
    return 'Forte'
  }
  
  const strengthColor = getStrengthColor()
  const strengthText = getStrengthText()
  
  const getBarColor = () => {
    if (strengthColor === 'red') return 'bg-red-500'
    if (strengthColor === 'yellow') return 'bg-yellow-500'
    return 'bg-green-500'
  }
  
  const getTextColor = () => {
    if (strengthColor === 'red') return 'text-red-600'
    if (strengthColor === 'yellow') return 'text-yellow-600'
    return 'text-green-600'
  }

  return (
    <div className="space-y-2">
      <label htmlFor={name} className="block text-xs font-medium text-gray-600 mb-2">
        Senha <span className="text-red-500">*</span>
      </label>
      <div className="relative">
        {icon && (
          <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
            {icon}
          </div>
        )}
        <input
          {...register(name)}
          id={name}
          type={showPassword ? 'text' : 'password'}
          placeholder={placeholder}
          className={`w-full ${icon ? 'pl-10' : 'pl-4'} pr-12 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-aviation-red-500 focus:border-transparent transition-all duration-300 ${
            hasError ? 'border-red-500 focus:border-red-500' : ''
          }`}
        />
        <button
          type="button"
          onClick={() => setShowPassword(!showPassword)}
          className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 transition-colors"
        >
          {showPassword ? (
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21"/>
            </svg>
          ) : (
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
            </svg>
          )}
        </button>
      </div>
      
        {isTouched && hasError && (
          <div className="mt-2 flex items-center space-x-2 text-sm text-red-600">
            <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
            </svg>
            <span>{hasError.message as string}</span>
          </div>
        )}
      
      {showValidation && password && (
        <div className="space-y-2">
          {/* Barra de força da senha */}
          <div className="flex items-center space-x-2">
            <div className="flex-1 bg-gray-200 rounded-full h-2">
              <div 
                className={`h-2 rounded-full transition-all duration-300 ${getBarColor()}`}
                style={{ width: `${(strength / 5) * 100}%` }}
              />
            </div>
            <span className={`text-xs font-medium ${getTextColor()}`}>
              {strengthText}
            </span>
          </div>
          
          {/* Lista de validações */}
          <div className="grid grid-cols-1 gap-1">
            <div className={`flex items-center space-x-2 text-xs ${
              checks.len ? 'text-green-600' : 'text-gray-500'
            }`}>
              <svg className={`w-3 h-3 ${checks.len ? 'text-green-500' : 'text-gray-400'}`} fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              </svg>
              <span>Mínimo 8 caracteres</span>
            </div>
            <div className={`flex items-center space-x-2 text-xs ${
              checks.digits ? 'text-green-600' : 'text-gray-500'
            }`}>
              <svg className={`w-3 h-3 ${checks.digits ? 'text-green-500' : 'text-gray-400'}`} fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              </svg>
              <span>Pelo menos 2 números</span>
            </div>
            <div className={`flex items-center space-x-2 text-xs ${
              checks.special ? 'text-green-600' : 'text-gray-500'
            }`}>
              <svg className={`w-3 h-3 ${checks.special ? 'text-green-500' : 'text-gray-400'}`} fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              </svg>
              <span>Pelo menos 1 caractere especial</span>
            </div>
            <div className={`flex items-center space-x-2 text-xs ${
              checks.upper ? 'text-green-600' : 'text-gray-500'
            }`}>
              <svg className={`w-3 h-3 ${checks.upper ? 'text-green-500' : 'text-gray-400'}`} fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              </svg>
              <span>Pelo menos 1 maiúscula</span>
            </div>
            <div className={`flex items-center space-x-2 text-xs ${
              checks.lower ? 'text-green-600' : 'text-gray-500'
            }`}>
              <svg className={`w-3 h-3 ${checks.lower ? 'text-green-500' : 'text-gray-400'}`} fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
              </svg>
              <span>Pelo menos 1 minúscula</span>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
