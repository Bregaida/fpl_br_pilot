"use client"

import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { fplSimplifiedSchema, type FplSimplifiedFormValues } from "../schemas/fpl-simplified-schema"
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
import { Switch } from "@/components/ui/switch"
import { Label } from "@/components/ui/label"
import { Slider } from "@/components/ui/slider"
import { toast } from "@/components/ui/use-toast"

interface FplSimplifiedFormProps {
  onSubmit: (data: FplSimplifiedFormValues) => void
  isSubmitting?: boolean
  onGeneratePreview?: (data: FplSimplifiedFormValues) => void
  onSubmitFpl?: (data: FplSimplifiedFormValues) => void
}

export function FplSimplifiedForm({ 
  onSubmit, 
  isSubmitting = false, 
  onGeneratePreview, 
  onSubmitFpl 
}: FplSimplifiedFormProps) {
  const form = useForm<z.infer<typeof fplSimplifiedSchema>>({
    resolver: zodResolver(fplSimplifiedSchema),
    defaultValues: {
      flightRules: "V",
      wakeTurbulence: "L",
      isLocalFlight: true,
      maxDistance: 25,
    },
  })

  // Watch for changes to show/hide fields conditionally
  const isLocalFlight = form.watch("isLocalFlight")
  const departureAerodrome = form.watch("departureAerodrome")

  // Set destination to departure when it's a local flight
  React.useEffect(() => {
    if (isLocalFlight && departureAerodrome) {
      form.setValue("destinationAerodrome", departureAerodrome)
    }
  }, [isLocalFlight, departureAerodrome, form])

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        {/* Flight Type Toggle */}
        <div className="flex items-center space-x-4 p-4 bg-muted/50 rounded-md">
          <div className="space-y-0.5">
            <Label htmlFor="isLocalFlight">Tipo de Voo</Label>
            <p className="text-sm text-muted-foreground">
              {isLocalFlight ? "Voo Local" : "Voo de Trajeto"}
            </p>
          </div>
          <FormField
            control={form.control}
            name="isLocalFlight"
            render={({ field }) => (
              <FormItem className="flex flex-row items-center justify-between">
                <FormControl>
                  <Switch
                    checked={field.value}
                    onCheckedChange={field.onChange}
                  />
                </FormControl>
              </FormItem>
            )}
          />
        </div>

        {/* Basic Aircraft Information */}
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

          <FormField
            control={form.control}
            name="aircraftType"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tipo de Aeronave</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: C172, PA28" {...field} className="uppercase" />
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
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Departure and Destination */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FormField
            control={form.control}
            name="departureAerodrome"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Aeródromo de Partida (ICAO)</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: SBAQ" {...field} className="uppercase" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          {!isLocalFlight && (
            <FormField
              control={form.control}
              name="destinationAerodrome"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Aeródromo de Destino (ICAO)</FormLabel>
                  <FormControl>
                    <Input 
                      placeholder="Ex: SBGR" 
                      {...field} 
                      className="uppercase"
                      disabled={isLocalFlight}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          )}

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

        {/* Flight Parameters */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <FormField
            control={form.control}
            name="cruisingSpeed"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Velocidade de Cruzeiro (opcional)</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: N090" {...field} className="uppercase" />
                </FormControl>
                <FormDescription>Ex: N090, K100, M082</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="cruisingLevel"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Nível de Voo (opcional)</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: F045" {...field} className="uppercase" />
                </FormControl>
                <FormDescription>Ex: F045, S030, A010</FormDescription>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="eet"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tempo de Voo (HHMM - opcional)</FormLabel>
                <FormControl>
                  <Input placeholder="HHMM" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {/* Local Flight Settings */}
        {isLocalFlight && (
          <div className="space-y-4 p-4 bg-muted/30 rounded-md">
            <div className="space-y-2">
              <div className="flex justify-between">
                <Label>Raio Máximo de Voo: {form.watch("maxDistance")}NM</Label>
              </div>
              <FormField
                control={form.control}
                name="maxDistance"
                render={({ field }) => (
                  <FormItem>
                    <FormControl>
                      <Slider
                        min={5}
                        max={50}
                        step={5}
                        defaultValue={[field.value]}
                        onValueChange={(value) => field.onChange(value[0])}
                        className="py-4"
                      />
                    </FormControl>
                    <FormDescription>
                      Defina o raio máximo para o voo local
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
          </div>
        )}

        {/* Route */}
        <FormField
          control={form.control}
          name="route"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Rota ou Área de Navegação</FormLabel>
              <FormControl>
                <Textarea
                  placeholder={isLocalFlight 
                    ? "Ex: Área de treinamento S, TMA de São Paulo, até 5.000ft"
                    : "Ex: VOR SP, BR-050, RIO PARANAÍBA"
                  }
                  className="min-h-[80px]"
                  {...field}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Additional Information */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="personsOnBoard"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Pessoas a Bordo (opcional)</FormLabel>
                <FormControl>
                  <Input type="number" min="1" placeholder="Ex: 2" {...field} />
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
                <FormLabel>Telefone de Contato (opcional)</FormLabel>
                <FormControl>
                  <Input placeholder="Ex: 5511987654321" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
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
              {isSubmitting 
                ? 'Enviando...' 
                : isLocalFlight 
                  ? 'Solicitar Autorização' 
                  : 'Enviar Plano de Voo'}
            </Button>
          ) : (
            <Button 
              type="submit" 
              disabled={isSubmitting}
            >
              {isSubmitting 
                ? 'Enviando...' 
                : isLocalFlight 
                  ? 'Solicitar Autorização' 
                  : 'Enviar Plano de Voo'}
            </Button>
          )}
        </div>
      </form>
    </Form>
  )
}
