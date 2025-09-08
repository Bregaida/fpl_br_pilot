import React from 'react'

type FFProps = React.InputHTMLAttributes<HTMLInputElement> & {
  label: string
  hint?: string
}

export function FormField({ label, hint, id, ...props }: FFProps) {
  const inputId = id || `ff-${label.replace(/\s+/g, '-').toLowerCase()}`
  return (
    <div className="flex flex-col gap-1">
      <label htmlFor={inputId} className="text-sm font-medium text-slate-700">{label}</label>
      <input id={inputId} {...props} className={`px-3 py-2 rounded-xl border border-slate-300 bg-white shadow-sm outline-none focus:ring-4 focus:ring-indigo-100 ${props.className || ''}`} />
      {hint && <p className="text-xs text-slate-500">{hint}</p>}
    </div>
  )
}
