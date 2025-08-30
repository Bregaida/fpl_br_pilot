'use client'

import React, { useState, useEffect } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Search, Map, AlertTriangle, Info } from 'lucide-react'
import { useToast } from '@/components/ui/use-toast'
import { useDebounce } from '@/hooks/use-debounce'
import { MetarDisplay } from '@/features/meteorology/components/metar-display'
import { TafDisplay } from '@/features/meteorology/components/taf-display'
import { SigmetAlerts, AirmetAlerts } from '@/features/meteorology/components/safety-alerts'
import { useMetar, useTaf, useSigmet, useAirmet } from '@/features/meteorology/hooks/use-meteorology-queries'

// Common FIRs in Brazil for quick selection
const BRAZILIAN_FIRS = [
  { id: 'SBAO', name: 'SBAO - Área de Controle de Área Oeste' },
  { id: 'SBAZ', name: 'SBAZ - Área de Controle de Área Centro' },
  { id: 'SBBS', name: 'SBBS - Brasilia FIR' },
  { id: 'SBCW', name: 'SBCW - Curitiba FIR' },
  { id: 'SBRE', name: 'SBRE - Recife FIR' },
  { id: 'SBAO', name: 'SBAO - Manaus FIR' },
  { id: 'SBAZ', name: 'SBAZ - Atlântico FIR' },
]

export default function MeteorologyPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const { toast } = useToast()
  
  // Get initial ICAO from URL params or use empty string
  const [icao, setIcao] = useState(searchParams.get('icao')?.toUpperCase() || '')
  const [fir, setFir] = useState('SBBS') // Default to Brasilia FIR
  const debouncedIcao = useDebounce(icao, 500)
  
  // Fetch weather data
  const {
    data: metar,
    isLoading: isMetarLoading,
    error: metarError,
  } = useMetar(debouncedIcao, !!debouncedIcao)
  
  const {
    data: taf,
    isLoading: isTafLoading,
    error: tafError,
  } = useTaf(debouncedIcao, !!debouncedIcao)
  
  const {
    data: sigmets = [],
    isLoading: isSigmetsLoading,
    error: sigmetsError,
  } = useSigmet(fir)
  
  const {
    data: airmets = [],
    isLoading: isAirmetsLoading,
    error: airmetsError,
  } = useAirmet(fir)
  
  // Update URL when ICAO changes
  useEffect(() => {
    if (debouncedIcao) {
      const params = new URLSearchParams(searchParams.toString())
      params.set('icao', debouncedIcao)
      router.push(`/meteorology?${params.toString()}`, { scroll: false })
    }
  }, [debouncedIcao, router, searchParams])
  
  // Handle search form submission
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    if (!icao) {
      toast({
        title: 'ICAO code is required',
        description: 'Please enter a valid ICAO code (e.g., SBGR, SBSP)',
        variant: 'destructive',
      })
      return
    }
    
    // The debounced effect will handle the actual search
    setIcao(icao.toUpperCase())
  }
  
  // Handle FIR change
  const handleFirChange = (newFir: string) => {
    setFir(newFir)
  }
  
  return (
    <div className="container mx-auto py-6 space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Meteorology</h1>
          <p className="text-muted-foreground">
            Get the latest weather information for airports and regions
          </p>
        </div>
        
        <form onSubmit={handleSearch} className="w-full md:w-auto flex gap-2">
          <div className="relative flex-1 md:w-64">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Enter ICAO code..."
              className="pl-8 w-full"
              value={icao}
              onChange={(e) => setIcao(e.target.value.toUpperCase())}
              maxLength={4}
            />
          </div>
          <Button type="submit">Search</Button>
        </form>
      </div>
      
      <Tabs defaultValue="airport" className="w-full">
        <TabsList className="grid w-full md:w-auto grid-cols-2 md:flex">
          <TabsTrigger value="airport">Airport Weather</TabsTrigger>
          <TabsTrigger value="region">Regional Weather</TabsTrigger>
          <TabsTrigger value="alerts">Warnings & Alerts</TabsTrigger>
          <TabsTrigger value="maps">Weather Maps</TabsTrigger>
        </TabsList>
        
        <TabsContent value="airport" className="mt-6">
          {!icao ? (
            <div className="flex flex-col items-center justify-center py-12 text-center">
              <Map className="h-12 w-12 text-muted-foreground mb-4" />
              <h3 className="text-lg font-medium">No airport selected</h3>
              <p className="text-sm text-muted-foreground max-w-md mt-2">
                Enter an ICAO code (e.g., SBGR, SBSP, SBGL) to view weather information for that airport.
              </p>
            </div>
          ) : (
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <MetarDisplay 
                data={metar} 
                isLoading={isMetarLoading} 
                error={metarError as Error} 
                icaoCode={icao} 
              />
              <TafDisplay 
                data={taf} 
                isLoading={isTafLoading} 
                error={tafError as Error} 
                icaoCode={icao} 
              />
            </div>
          )}
        </TabsContent>
        
        <TabsContent value="region" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Regional Weather</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <h3 className="text-lg font-medium mb-2">Select Flight Information Region (FIR)</h3>
                  <div className="flex flex-wrap gap-2">
                    {BRAZILIAN_FIRS.map((firOption) => (
                      <Button
                        key={firOption.id}
                        variant={fir === firOption.id ? 'default' : 'outline'}
                        size="sm"
                        onClick={() => handleFirChange(firOption.id)}
                      >
                        {firOption.name}
                      </Button>
                    ))}
                  </div>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-6">
                  <div className="space-y-4">
                    <h3 className="text-lg font-medium flex items-center gap-2">
                      <AlertTriangle className="h-5 w-5 text-destructive" />
                      SIGMETs
                    </h3>
                    <SigmetAlerts 
                      alerts={sigmets} 
                      isLoading={isSigmetsLoading} 
                      error={sigmetsError as Error}
                    />
                  </div>
                  
                  <div className="space-y-4">
                    <h3 className="text-lg font-medium flex items-center gap-2">
                      <AlertTriangle className="h-5 w-5 text-amber-500" />
                      AIRMETs
                    </h3>
                    <AirmetAlerts 
                      alerts={airmets} 
                      isLoading={isAirmetsLoading} 
                      error={airmetsError as Error}
                    />
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="alerts" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Weather Warnings & Alerts</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-6">
                <div className="p-4 bg-muted/50 rounded-lg">
                  <h3 className="font-medium flex items-center gap-2 mb-2">
                    <Info className="h-4 w-4" />
                    About Weather Alerts
                  </h3>
                  <p className="text-sm text-muted-foreground">
                    This section provides information about significant weather phenomena that may affect flight safety, 
                    including SIGMETs, AIRMETs, and other aviation weather advisories for the selected region.
                  </p>
                </div>
                
                <div className="space-y-4">
                  <h3 className="text-lg font-medium">Active Warnings for {fir}</h3>
                  
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-4">
                      <h4 className="font-medium flex items-center gap-2">
                        <AlertTriangle className="h-4 w-4 text-destructive" />
                        SIGMETs (Significant Meteorological Information)
                      </h4>
                      <SigmetAlerts 
                        alerts={sigmets} 
                        isLoading={isSigmetsLoading} 
                        error={sigmetsError as Error}
                        emptyMessage="No active SIGMETs for this region"
                      />
                    </div>
                    
                    <div className="space-y-4">
                      <h4 className="font-medium flex items-center gap-2">
                        <AlertTriangle className="h-4 w-4 text-amber-500" />
                        AIRMETs (Airmen's Meteorological Information)
                      </h4>
                      <AirmetAlerts 
                        alerts={airmets} 
                        isLoading={isAirmetsLoading} 
                        error={airmetsError as Error}
                        emptyMessage="No active AIRMETs for this region"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="maps" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Weather Maps</CardTitle>
              <p className="text-sm text-muted-foreground">
                Interactive weather maps and satellite imagery
              </p>
            </CardHeader>
            <CardContent>
              <div className="aspect-video bg-muted rounded-lg flex items-center justify-center">
                <div className="text-center">
                  <Map className="h-12 w-12 mx-auto text-muted-foreground mb-2" />
                  <h3 className="font-medium">Weather Maps</h3>
                  <p className="text-sm text-muted-foreground max-w-md mt-1">
                    Interactive weather maps and satellite imagery will be displayed here.
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </TabsContent>
      </Tabs>
    </div>
  )
}
