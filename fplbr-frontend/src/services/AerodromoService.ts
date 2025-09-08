// Serviço mock para capacidades de aeródromos

const NOTURNO_AUTORIZADO: Record<string, boolean> = {
  SBMT: true,
  SBJD: false,
  SBSJ: true,
}

export async function vooNoturnoAutorizado(icao: string): Promise<boolean> {
  const key = (icao || '').toUpperCase()
  if (!key) return Promise.resolve(false)
  return Promise.resolve(NOTURNO_AUTORIZADO[key] ?? false)
}


