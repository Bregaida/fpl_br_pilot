import { z } from "zod";

// Common validation patterns
const icaoPattern = /^[A-Z]{4}$/;
const timePattern = /^([01]\d|2[0-3])[0-5]\d$/; // HHMM (0000-2359)
const speedPattern = /^([NK]\d{4}|M\d{3})$/; // N/K followed by 4 digits or M followed by 3 digits
const levelPattern = /^([FSA]\d{3})$/; // F/S/A followed by 3 digits
const aircraftIdPattern = /^[A-Z0-9]{2,7}$/; // 2-7 alphanumeric chars, uppercase

// Base FPL schema
export const fplSchema = z.object({
  // Item 7: Aircraft Identification and SSR Mode and Code
  aircraftId: z.string()
    .min(2, "Mínimo 2 caracteres")
    .max(7, "Máximo 7 caracteres")
    .regex(aircraftIdPattern, "Apenas letras e números")
    .transform(val => val.toUpperCase()),
    
  // Item 8: Flight Rules and Type of Flight
  flightRules: z.enum(["I", "V", "Y", "Z"], {
    required_error: "Selecione as regras de voo",
  }),
  flightType: z.string().min(1, "Tipo de voo é obrigatório"),
  
  // Item 9: Number, Type of Aircraft and Wake Turbulence Category
  aircraftType: z.string().min(1, "Tipo de aeronave é obrigatório"),
  wakeTurbulence: z.enum(["L", "M", "H", "J"], {
    required_error: "Categoria de esteira é obrigatória",
  }),
  equipmentSuffix: z.string().optional(),
  
  // Item 10: Equipment
  equipment: z.string().optional(),
  surveillanceEquipment: z.string().optional(),
  
  // Item 13: Departure Aerodrome and Time
  departureAerodrome: z.string()
    .length(4, "Código ICAO deve ter 4 letras")
    .regex(icaoPattern, "Código ICAO inválido")
    .transform(val => val.toUpperCase()),
  departureTime: z.string()
    .regex(timePattern, "Hora inválida (use HHMM em UTC)"),
    
  // Item 15: Route
  cruisingSpeed: z.string()
    .regex(speedPattern, "Formato inválido (ex: N0450, K0450, M082)"),
  cruisingLevel: z.string()
    .regex(levelPattern, "Formato inválido (ex: F100, S090, A050)"),
  route: z.string().min(1, "Rota é obrigatória"),
  
  // Item 16: Destination Aerodrome and Total Estimated Elapsed Time
  destinationAerodrome: z.string()
    .length(4, "Código ICAO deve ter 4 letras")
    .regex(icaoPattern, "Código ICAO inválido")
    .transform(val => val.toUpperCase()),
  eet: z.string()
    .regex(/^[0-9]{4}$/, "Tempo de voo inválido (use HHMM)"),
  
  // Item 18: Other Information
  alternates: z.array(
    z.string()
      .length(4, "Código ICAO deve ter 4 letras")
      .regex(icaoPattern, "Código ICAO inválido")
      .transform(val => val.toUpperCase())
  ).max(2, "Máximo 2 aeródromos alternativos").optional(),
  
  dof: z.string()
    .regex(/^\d{6}$/, "Data inválida (use DDMMYY)")
    .optional(),
  operator: z.string().optional(),
  pbn: z.string().optional(),
  nav: z.string().optional(),
  com: z.string().optional(),
  dat: z.string().optional(),
  sur: z.string().optional(),
  remarks: z.string().optional(),
  
  // Item 19: Supplementary Information
  endurance: z.string()
    .regex(/^[0-9]{4}$/, "Autonomia inválida (use HHMM)")
    .optional(),
  personsOnBoard: z.string()
    .regex(/^\d+$/, "Número inválido")
    .optional(),
  emergencyRadio: z.string().optional(),
  survivalEquipment: z.string().optional(),
  lifeJackets: z.string().optional(),
  dinghies: z.string().optional(),
  aircraftColor: z.string().optional(),
  contactNumber: z.string().optional(),
});

export type FplFormValues = z.infer<typeof fplSchema>;
