import { useFormContext } from 'react-hook-form'

interface PasswordFieldProps {
  name: string
  label: string
  placeholder?: string
  required?: boolean
  showValidation?: boolean
}

export default function PasswordField({ 
  name, 
  label, 
  placeholder = 'Digite sua senha',
  required = false,
  showValidation = true
}: PasswordFieldProps) {
  const { register, watch, formState: { errors, touchedFields, dirtyFields } } = useFormContext()
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

  return (
    <div className="grid grid-cols-3 gap-3 items-start">
      <label className="text-sm text-slate-600 mt-2">
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <div className="col-span-2 grid gap-2">
        <input
          {...register(name)}
          type="password"
          placeholder={placeholder}
          className={`input ${hasError ? 'border-red-500 focus:border-red-500' : ''}`}
        />
        {isTouched && hasError && (
          <div className="text-red-600 text-xs mt-1">
            {hasError.message as string}
          </div>
        )}
        {showValidation && password && (
          <ul className="text-xs space-y-1">
            <li className={checks.len ? 'text-emerald-700' : 'text-red-700'}>
              Mínimo 8 caracteres
            </li>
            <li className={checks.digits ? 'text-emerald-700' : 'text-red-700'}>
              Pelo menos 2 números
            </li>
            <li className={checks.special ? 'text-emerald-700' : 'text-red-700'}>
              Pelo menos 1 caractere especial
            </li>
            <li className={checks.upper ? 'text-emerald-700' : 'text-red-700'}>
              Pelo menos 1 maiúscula
            </li>
            <li className={checks.lower ? 'text-emerald-700' : 'text-red-700'}>
              Pelo menos 1 minúscula
            </li>
          </ul>
        )}
      </div>
    </div>
  )
}
