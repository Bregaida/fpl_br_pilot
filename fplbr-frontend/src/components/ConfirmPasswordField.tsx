import { useFormContext } from 'react-hook-form'

interface ConfirmPasswordFieldProps {
  name: string
  label: string
  passwordFieldName: string
  placeholder?: string
  required?: boolean
}

export default function ConfirmPasswordField({ 
  name, 
  label, 
  passwordFieldName,
  placeholder = 'Confirme sua senha',
  required = false
}: ConfirmPasswordFieldProps) {
  const { register, watch, formState: { errors, touchedFields, dirtyFields } } = useFormContext()
  const hasError = errors[name]
  const isTouched = touchedFields[name] || dirtyFields[name]
  const password = watch(passwordFieldName) || ''
  const confirmPassword = watch(name) || ''
  const passwordsMatch = confirmPassword && password === confirmPassword

  return (
    <div className="grid grid-cols-3 gap-3 items-start">
      <label className="text-sm text-slate-600 mt-2">
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <div className="col-span-2 grid gap-1">
        <input
          {...register(name)}
          type="password"
          placeholder={placeholder}
          className={`input ${hasError ? 'border-red-500 focus:border-red-500' : ''}`}
          onPaste={(e) => e.preventDefault()}
        />
        {isTouched && hasError && (
          <div className="text-red-600 text-xs mt-1">
            {hasError.message as string}
          </div>
        )}
        {confirmPassword && (
          <div className={`text-xs ${passwordsMatch ? 'text-emerald-700' : 'text-red-700'}`}>
            {passwordsMatch ? 'Senhas conferem' : 'Senhas não conferem'}
          </div>
        )}
      </div>
    </div>
  )
}
