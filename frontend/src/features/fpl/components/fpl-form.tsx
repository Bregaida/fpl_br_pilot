"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { z } from "zod"
import { fplSchema, type FplFormValues } from "../schemas/fpl-schema"
import { Button } from "@/components/ui/button"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Textarea } from "@/components/ui/textarea"
import { toast } from "@/components/ui/use-toast"

interface FplFormProps {
  onSubmit: (data: FplFormValues) => void
  isSubmitting?: boolean
  onGeneratePreview?: (data: FplFormValues) => void
  onSubmitFpl?: (data: FplFormValues) => void
}

export function FplForm({ 
  onSubmit, 
  isSubmitting = false, 
  onGeneratePreview, 
  onSubmitFpl 
}: FplFormProps) {
  const form = useForm<z.infer<typeof fplSchema>>({
    resolver: zodResolver(fplSchema),
    defaultValues: {
      flightRules: "I",
      wakeTurbulence: "M",
      alternates: [],
    },
  })

  // Watch for changes to show/hide fields conditionally
  const flightRules = form.watch("flightRules")
  const showIfrFields = flightRules === "I" || flightRules === "Y"

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
        {/* Item 7: Aircraft Identification */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FormField
            control={form.control}
            name="aircraftId"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Identificação da Aeronave</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: PTABC" {...field} className="uppercase" />
                </FormControl>
                <FormDescription>2-7 caracteres alfanuméricos</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />

          {/* Item 8: Flight Rules */}
          <FormField
            control={form.control}
            name="flightRules"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Regras de Voo</FormLabel>
                <Select onValueChange={field.onChange} defaultValue={field.value}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="I">IFR</SelectItem>
                    <SelectItem value="V">VFR</SelectItem>
                    <SelectItem value="Y">IFR (mudando para VFR)</SelectItem>
                    <SelectItem value="Z">VFR (mudando para IFR)</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />

          {/* Item 8: Type of Flight */}
          <FormField
            control={form.control}
            name="flightType"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tipo de Voo</FormLabel>
                <Select onValueChange={field.onChange} defaultValue={field.value}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="S">Agendado</SelectItem>
                    <SelectItem value="N">Não Agendado</SelectItem>
                    <SelectItem value="G">Serviço Aéreo Geral</SelectItem>
                    <SelectItem value="M">Militar</SelectItem>
                    <SelectItem value="X">Outro</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Item 9: Aircraft Type and Wake Turbulence */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FormField
            control={form.control}
            name="aircraftType"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tipo de Aeronave</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: C172, A320" {...field} className="uppercase" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="wakeTurbulence"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Categoria de Esteira</FormLabel>
                <Select onValueChange={field.onChange} defaultValue={field.value}>
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="L">L (Light)</SelectItem>
                    <SelectItem value="M">M (Medium)</SelectItem>
                    <SelectItem value="H">H (Heavy)</SelectItem>
                    <SelectItem value="J">J (Super)</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="equipmentSuffix"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Sufixo de Equipamento (opcional)</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: /W, /S" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Item 10: Equipment */}
        {showIfrFields && (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField
              control={form.control}
              name="equipment"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Equipamento (opcional)</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: SDFG" {...field} />
                  </FormControl>
                  <FormDescription>Equipamentos de navegação</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="surveillanceEquipment"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Equipamento de Vigilância (opcional)</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: S" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
        )}

        {/* Item 13: Departure */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FormField
            control={form.control}
            name="departureAerodrome"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Aeródromo de Partida (ICAO)</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: SBGR" {...field} className="uppercase" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="departureTime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Hora de Partida (UTC)</FormLabel>
                <FormControl>
                  <Input placeholder="HHMM" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Item 15: Route */}
        <div className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField
              control={form.control}
              name="cruisingSpeed"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Velocidade de Cruzeiro</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: N0450, K0450, M082" {...field} className="uppercase" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="cruisingLevel"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nível de Cruzeiro</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: F100, S090, A050" {...field} className="uppercase" />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <FormField
            control={form.control}
            name="route"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Rota</FormLabel>
                <FormControl>
                  <Textarea
                    placeholder="Ex: DCT UZ31 PAB DCT"
                    className="min-h-[80px]"
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Item 16: Destination */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FormField
            control={form.control}
            name="destinationAerodrome"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Aeródromo de Destino (ICAO)</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: SBGL" {...field} className="uppercase" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="eet"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tempo Total de Voo (HHMM)</FormLabel>
                <FormControl>
                  <Input placeholder="HHMM" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Item 18: Other Information */}
        <div className="space-y-4">
          <h3 className="text-lg font-medium">Outras Informações (Item 18)</h3>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField
              control={form.control}
              name="dof"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Data do Voo (DDMMYY)</FormLabel>
                  <FormControl>
                    <Input placeholder="DDMMYY" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="operator"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Operador (OPR/ - opcional)</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: TAM" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField
              control={form.control}
              name="pbn"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>PBN/</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: B1B2C1D1L1" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="nav"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>NAV/</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: F70R" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="com"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>COM/</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: PBN NAV" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <FormField
            control={form.control}
            name="remarks"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Observações (RMK/)</FormLabel>
                <FormControl>
                  <Textarea
                    placeholder="Observações adicionais"
                    className="min-h-[60px]"
                    {...field}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Item 19: Supplementary Information */}
        <div className="space-y-4">
          <h3 className="text-lg font-medium">Informações Suplementares (Item 19)</h3>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <FormField
              control={form.control}
              name="endurance"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Autonomia (E/ - HHMM)</FormLabel>
                  <FormControl>
                    <Input placeholder="HHMM" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="personsOnBoard"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Pessoas a Bordo (P/)</FormLabel>
                  <FormControl>
                    <Input placeholder="Número de pessoas" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <FormField
              control={form.control}
              name="aircraftColor"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Cor e Marcações (C/)</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: BRANCO/VERMELHO" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="contactNumber"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Telefone de Contato (T/)</FormLabel>
                  <FormControl>
                    <Input placeholder="Ex: 5511987654321" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>
        </div>

        <div className="flex justify-end gap-4 pt-4">
          {onGeneratePreview && (
            <Button 
              type="button" 
              variant="outline"
              onClick={form.handleSubmit(onGeneratePreview)}
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Gerando...' : 'Pré-visualizar'}
            </Button>
          )}
          
          {onSubmitFpl ? (
            <Button 
              type="button"
              onClick={form.handleSubmit(onSubmitFpl)}
              disabled={isSubmitting}
            >
              {isSubmitting ? 'Enviando...' : 'Enviar Plano de Voo'}
            </Button>
          ) : (
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? 'Enviando...' : 'Enviar'}
            </Button>
          )}
        </div>
      </form>
    </Form>
  )
}
