import { apiRequest } from './api';
import { FplForm, FplComposedResponse, AerodromoInfo } from '@/types';

/**
 * Compose a flight plan with all related data
 * @param form The flight plan form data
 * @returns Promise with the composed flight plan response
 */
export async function composeFpl(form: FplForm): Promise<FplComposedResponse> {
  return apiRequest<FplComposedResponse>({
    method: 'POST',
    url: '/api/compose/fpl',
    data: form,
  });
}

/**
 * Get aerodrome information by ICAO code and date of flight (DOF)
 * @param icao The ICAO code of the aerodrome
 * @param dof The date of flight in YYYYMMDD format
 * @returns Promise with the aerodrome information
 */
export async function getAerodromeInfo(icao: string, dof: string): Promise<AerodromoInfo> {
  return apiRequest<AerodromoInfo>({
    method: 'GET',
    url: `/api/lookup/aerodromo`,
    params: { icao, dof },
  });
}

/**
 * Get weather briefing (METAR/TAF) for an aerodrome
 * @param icao The ICAO code of the aerodrome
 * @returns Promise with the weather briefing data
 */
export async function getWeatherBriefing(icao: string): Promise<{
  raw: { metar?: string; taf?: string; updatedAt?: string };
  decoded: { metar?: any; taf?: any };
}> {
  return apiRequest({
    method: 'GET',
    url: '/api/briefing',
    params: { icao },
  });
}

/**
 * Get NOTAMs for an aerodrome
 * @param icao The ICAO code of the aerodrome
 * @returns Promise with the list of NOTAMs
 */
export async function getNotams(icao: string): Promise<Array<{
  id: string;
  from?: string;
  to?: string;
  texto: string;
}>> {
  return apiRequest({
    method: 'GET',
    url: '/api/notams',
    params: { icao },
  });
}

/**
 * Get all data needed for the flight plan in a single request
 * @param form The flight plan form data
 * @returns Promise with all the data needed for the flight plan
 */
export async function getFlightPlanData(form: FplForm): Promise<FplComposedResponse> {
  return composeFpl(form);
}
