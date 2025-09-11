import React from 'react'

type Props = { value: string; onChange: (v: string)=>void }

export default function TotpInput({ value, onChange }: Props) {
  const refs = React.useRef<Array<HTMLInputElement|null>>([])
  const vals = (value.padEnd(6, ' ').slice(0,6).split(''))
  function setDigit(i: number, ch: string) {
    const v = (value + ch).slice(0, i).padEnd(i,'') + ch + (value.slice(i+1))
    onChange(v.replace(/\D/g,''))
    const next = refs.current[i+1]; if (next) next.focus()
  }
  function onKey(i:number, e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === 'Backspace') {
      const prev = refs.current[i-1]; if (prev) prev.focus()
      onChange(value.slice(0, Math.max(0,i-1)))
    }
  }
  return (
    <div className="flex gap-2" aria-label="Código TOTP">
      {vals.map((ch, i) => (
        <input key={i}
          ref={el => refs.current[i]=el}
          className="w-10 h-12 text-center border rounded"
          value={/\d/.test(ch) ? ch : ''}
          inputMode="numeric"
          aria-label={`Dígito ${i+1}`}
          maxLength={1}
          onChange={e => { const d = e.target.value.replace(/\D/g,''); if (d) setDigit(i, d) }}
          onKeyDown={e => onKey(i,e)} />
      ))}
    </div>
  )
}


