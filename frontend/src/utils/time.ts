// Funções utilitárias de data/hora em UTC e durações HHmm

export function nowUtcISO(): string {
  return new Date().toISOString();
}

export function addMinutesUtc(isoUtc: string, minutes: number): string {
  const d = new Date(isoUtc);
  d.setUTCMinutes(d.getUTCMinutes() + minutes);
  return d.toISOString();
}

export function parseHHmmToMinutes(hhmm: string): number {
  const m = /^([0-1]\d|2[0-3])([0-5]\d)$/.exec(hhmm);
  if (!m) return NaN;
  const h = parseInt(m[1], 10);
  const min = parseInt(m[2], 10);
  return h * 60 + min;
}

export function minutesToHHmm(total: number): string {
  const h = Math.floor(total / 60);
  const m = total % 60;
  const hh = (h < 10 ? '0' : '') + h;
  const mm = (m < 10 ? '0' : '') + m;
  return `${hh}${mm}`;
}

export function toUtcIsoFromLocalDateTimeLocal(input: string): string {
  // input esperado no formato "YYYY-MM-DDTHH:mm" (datetime-local)
  // Converte para Date no fuso local e exporta para ISO UTC
  if (!input) return '';
  const d = new Date(input);
  return d.toISOString();
}

export function todayDdmmaaUtc(): string {
  const d = new Date();
  const dd = (d.getUTCDate() < 10 ? '0' : '') + d.getUTCDate();
  const mm = (d.getUTCMonth() + 1 < 10 ? '0' : '') + (d.getUTCMonth() + 1);
  const aa = (d.getUTCFullYear() % 100).toString().padStart(2, '0');
  return `${dd}${mm}${aa}`;
}

export function isFutureAtLeastMinutes(isoUtc: string, minutesAhead: number): boolean {
  try {
    const now = new Date();
    const min = new Date(now.toISOString());
    min.setUTCMinutes(min.getUTCMinutes() + minutesAhead);
    const target = new Date(isoUtc);
    return target.getTime() >= min.getTime();
  } catch {
    return false;
  }
}


