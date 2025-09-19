import React from 'react'
import { useFormContext } from 'react-hook-form'

interface FormFieldProps {
  name: string
  label: string
  type?: string
  placeholder?: string
  required?: boolean
  className?: string
  children?: React.ReactNode
  onBlur?: () => void
  min?: number
  max?: number
}

export default function FormField({ 
  name, 
  label, 
  type = 'text', 
  placeholder, 
  required = false,
  className = '',
  children,
  onBlur,
  min,
  max
}: FormFieldProps) {
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
        {children || (
          <input
            {...register(name, {
              onBlur: onBlur
            })}
            type={type}
            placeholder={placeholder}
            className={`input ${hasError ? 'border-red-500 focus:border-red-500' : ''}`}
            min={min}
            max={max}
          />
        )}
        {isTouched && hasError && (
          <div className="text-red-600 text-xs mt-1">
            {hasError.message as string}
          </div>
        )}
      </div>
    </div>
  )
}