import React from 'react'

interface Option { value: string; label: React.ReactNode; tooltip?: string }

interface Props {
  name: string
  label: React.ReactNode
  options: Option[]
  value: string
  onChange: (val: string) => void
  error?: string
}

export default function RadioGroup({ name, label, options, value, onChange, error }: Props) {
  return (
    <div className="grid gap-1">
      <label className="font-medium">{label}</label>
      <div className="flex flex-wrap gap-3">
        {options.map(opt => (
          <label key={opt.value} className="inline-flex items-center gap-2">
            <input
              type="radio"
              name={name}
              value={opt.value}
              checked={value === opt.value}
              onChange={(e) => onChange(e.target.value)}
              className="accent-indigo-600"
            />
            <span>
              {opt.label}
              {opt.tooltip && <sup title={opt.tooltip} className="text-slate-500 ml-1 font-semibold">¹</sup>}
            </span>
          </label>
        ))}
      </div>
      {error && <p className="text-red-600 text-sm">{error}</p>}
    </div>
  )
}


