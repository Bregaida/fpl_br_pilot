import React from 'react'

interface Props {
  perfil: 'VFR' | 'IFR'
  rumoMagnetico?: number
}

function gerarListaVfr(rumo?: number): string[] {
  const impares = ['035','055','075','095','115','135','155','175']
  const pares = ['045','065','085','105','125','145','165','185']
  if (rumo == null || isNaN(rumo)) return impares.concat(pares).map(f => `FL ${f}`)
  if (rumo >= 0 && rumo < 180) return impares.map(f => `FL ${f}`)
  return pares.map(f => `FL ${f}`)
}

function gerarListaIfr(rumo?: number): string[] {
  const baseImpares = [30,50,70,90,110,130,150,170,190,210,230,250,270,290,330,370,410,450,490,510,530,550]
  const basePares = [20,40,60,80,100,120,140,160,180,200,220,240,260,280,310,350,390,430,470,490,510,530,550,570,590]
  const toLabel = (n: number) => `FL ${String(n).padStart(3,'0')}`
  if (rumo == null || isNaN(rumo)) return baseImpares.map(toLabel).concat(basePares.map(toLabel))
  if (rumo >= 0 && rumo < 180) return baseImpares.map(toLabel)
  return basePares.map(toLabel)
}

export default function NivelAssist({ perfil, rumoMagnetico }: Props) {
  const lista = (perfil === 'VFR') ? gerarListaVfr(rumoMagnetico) : gerarListaIfr(rumoMagnetico)
  return (
    <div className="grid gap-2">
      <div className="text-sm text-slate-600">Sugestões de FL com base no rumo e regra de voo (consulta):</div>
      <div className="flex flex-wrap gap-2">
        {lista.map(fl => (
          <span key={fl} className="px-2 py-1 rounded border text-xs bg-slate-50">{fl}</span>
        ))}
      </div>
    </div>
  )
}


