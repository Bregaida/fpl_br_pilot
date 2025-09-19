import { useFormContext } from 'react-hook-form'

interface SelectOption {
  value: string
  label: string
}

interface SelectFieldProps {
  name: string
  label: string
  options: SelectOption[]
  placeholder?: string
  required?: boolean
  className?: string
}

export default function SelectField({ 
  name, 
  label, 
  options,
  placeholder = 'Selecione',
  required = false,
  className = ''
}: SelectFieldProps) {
  const { register, formState: { errors, touchedFields, dirtyFields } } = useFormContext()
  const hasError = errors[name]
  const isTouched = touchedFields[name] || dirtyFields[name]

  return (
    <div className={`grid grid-cols-3 gap-3 items-start ${className}`}>
      <label className="text-sm text-slate-600 mt-2">
        {label}
        {required && <span className="text-red-500 ml-1">*</span>}
      </label>
      <div className="col-span-2 grid gap-1">
        <select
          {...register(name)}
          className={`input ${hasError ? 'border-red-500 focus:border-red-500' : ''}`}
        >
          <option value="">{placeholder}</option>
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        {isTouched && hasError && (
          <div className="text-red-600 text-xs mt-1">
            {hasError.message as string}
          </div>
        )}
      </div>
    </div>
  )
}
