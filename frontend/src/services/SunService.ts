// Serviço mock para nascer/pôr do sol em UTC e detecção de janela noturna

export interface SunInfo {
  sunriseUtc: string // ISO UTC
  sunsetUtc: string  // ISO UTC
}

function mockSun(_icao: string, dofYyMmDd: string): SunInfo {
  // Mock simples: sunrise 09:30Z, sunset 21:30Z na data informada (UTC)
  const yy = dofYyMmDd.slice(4, 6)
  const mm = dofYyMmDd.slice(2, 4)
  const dd = dofYyMmDd.slice(0, 2)
  const year = Number(yy) + 2000
  const month = Number(mm) - 1
  const day = Number(dd)
  const sunrise = new Date(Date.UTC(year, month, day, 9, 30, 0))
  const sunset = new Date(Date.UTC(year, month, day, 21, 30, 0))
  return { sunriseUtc: sunrise.toISOString(), sunsetUtc: sunset.toISOString() }
}

export async function getSunInfo(icao: string, dofYyMmDd: string): Promise<SunInfo> {
  // Trocar para chamada real quando backend expor /aerodromos/{icao}/sol?dof=YYMMDD
  return Promise.resolve(mockSun(icao, dofYyMmDd))
}

export function isNoturnoPorJanela(horaPartidaIsoUtc: string, eetHHmm: string, sun: SunInfo): boolean {
  const dep = new Date(horaPartidaIsoUtc).getTime()
  const m = /^([0-1]\d|2[0-3])([0-5]\d)$/.exec(eetHHmm)
  if (!m) return false
  const mins = parseInt(m[1], 10) * 60 + parseInt(m[2], 10)
  const arr = dep + mins * 60_000
  const rise = new Date(sun.sunriseUtc).getTime()
  const set = new Date(sun.sunsetUtc).getTime()
  // Considera noturno se decolagem ou chegada estiver fora do intervalo [sunrise, sunset]
  const depNoturno = dep < rise || dep > set
  const arrNoturno = arr < rise || arr > set
  return depNoturno || arrNoturno
}


