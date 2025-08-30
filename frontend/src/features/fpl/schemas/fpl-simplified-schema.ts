import { z } from "zod";

// Reuse patterns from the full schema
const icaoPattern = /^[A-Z]{4}$/;
const timePattern = /^([01]\d|2[0-3])[0-5]\d$/; // HHMM (0000-2359)
const speedPattern = /^([NK]\d{4}|M\d{3})$/; // N/K followed by 4 digits or M followed by 3 digits
const levelPattern = /^([FSA]\d{3})$/; // F/S/A followed by 3 digits
const aircraftIdPattern = /^[A-Z0-9]{2,7}$/; // 2-7 alphanumeric chars, uppercase

// Simplified FPL schema for VFR/local flights
export const fplSimplifiedSchema = z.object({
  // Item 7: Aircraft Identification
  aircraftId: z.string()
    .min(2, "Mínimo 2 caracteres")
    .max(7, "Máximo 7 caracteres")
    .regex(aircraftIdPattern, "Apenas letras e números")
    .transform(val => val.toUpperCase()),
    
  // Item 8: Flight Rules (simplified to VFR for local flights)
  flightRules: z.literal("V"),
  
  // Item 9: Number, Type of Aircraft and Wake Turbulence Category
  aircraftType: z.string().min(1, "Tipo de aeronave é obrigatório"),
  wakeTurbulence: z.enum(["L", "M"], {
    required_error: "Categoria de esteira é obrigatória",
  }),
  
  // Item 10: Equipment (simplified for VFR)
  equipment: z.string().optional(),
  
  // Item 13: Departure Aerodrome and Time
  departureAerodrome: z.string()
    .length(4, "Código ICAO deve ter 4 letras")
    .regex(icaoPattern, "Código ICAO inválido")
    .transform(val => val.toUpperCase()),
  departureTime: z.string()
    .regex(timePattern, "Hora inválida (use HHMM em UTC)"),
    
  // Item 15: Route (simplified for local flights)
  cruisingSpeed: z.string()
    .regex(speedPattern, "Formato inválido (ex: N0450, K0450, M082)")
    .optional(),
  cruisingLevel: z.string()
    .regex(levelPattern, "Formato inválido (ex: F100, S090, A050)")
    .optional(),
  route: z.string().min(1, "Rota é obrigatória"),
  
  // Item 16: Destination Aerodrome (same as departure for local flights)
  destinationAerodrome: z.string()
    .length(4, "Código ICAO deve ter 4 letras")
    .regex(icaoPattern, "Código ICAO inválido")
    .transform(val => val.toUpperCase()),
  eet: z.string()
    .regex(/^[0-9]{4}$/, "Tempo de voo inválido (use HHMM)")
    .optional(),
  
  // Item 19: Supplementary Information (simplified)
  personsOnBoard: z.string()
    .regex(/^\d+$/, "Número inválido")
    .optional(),
  contactNumber: z.string().optional(),
  
  // Additional fields for simplified form
  isLocalFlight: z.boolean().default(false),
  maxDistance: z.number().min(25).max(50).default(25),
});

export type FplSimplifiedFormValues = z.infer<typeof fplSimplifiedSchema>;
