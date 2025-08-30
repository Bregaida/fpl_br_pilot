'use client'

import React, { useState, useEffect } from 'react'
import { useRouter, useSearchParams } from 'next/navigation'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Input } from '@/components/ui/input'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card'
import { Search, MapPin, Plane, Radio, Navigation, FileText, AlertTriangle } from 'lucide-react'
import { useToast } from '@/components/ui/use-toast'
import { useDebounce } from '@/hooks/use-debounce'
import { DataTable } from '@/components/ui/data-table'
import { columns as airportColumns } from '@/features/airports/components/airport-columns'
import { Airport } from '@/features/airports/types/airport'
import { useSearchAirports, useCountriesWithAirports } from '@/features/airports/hooks/use-airports-queries'
import { AirportDetailsView } from '@/features/airports/components/airport-details-view'

export default function AirportsPage() {
  const router = useRouter()
  const searchParams = useSearchParams()
  const { toast } = useToast()
  
  // Get initial search query from URL params
  const initialQuery = searchParams.get('q') || ''
  const [searchQuery, setSearchQuery] = useState(initialQuery)
  const [selectedAirport, setSelectedAirport] = useState<Airport | null>(null)
  const [activeTab, setActiveTab] = useState('search')
  const debouncedSearchQuery = useDebounce(searchQuery, 500)
  
  // Fetch airports data
  const { 
    data: airportsData, 
    isLoading, 
    error 
  } = useSearchAirports(
    { query: debouncedSearchQuery },
    !!debouncedSearchQuery && debouncedSearchQuery.length >= 2
  )
  
  // Fetch countries for filter
  const { data: countries } = useCountriesWithAirports(true)
  
  // Update URL when search query changes
  useEffect(() => {
    if (debouncedSearchQuery) {
      const params = new URLSearchParams(searchParams.toString())
      params.set('q', debouncedSearchQuery)
      router.push(`/airports?${params.toString()}`, { scroll: false })
    }
  }, [debouncedSearchQuery, router, searchParams])
  
  // Handle search form submission
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    if (!searchQuery) {
      toast({
        title: 'Search query is required',
        description: 'Please enter an airport name, city, or ICAO/IATA code',
        variant: 'destructive',
      })
      return
    }
    
    // The debounced effect will handle the actual search
    setSearchQuery(searchQuery.trim())
  }
  
  // Handle airport selection
  const handleSelectAirport = (airport: Airport) => {
    setSelectedAirport(airport)
    setActiveTab('details')
  }
  
  return (
    <div className="container mx-auto py-6 space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Airports & Charts</h1>
          <p className="text-muted-foreground">
            Search for airports, view charts, NOTAMs, and other aeronautical information
          </p>
        </div>
        
        <form onSubmit={handleSearch} className="w-full md:w-auto flex gap-2">
          <div className="relative flex-1 md:w-96">
            <Search className="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Search by airport name, city, or code..."
              className="pl-8 w-full"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
          <Button type="submit">Search</Button>
        </form>
      </div>
      
      <Tabs 
        value={activeTab} 
        onValueChange={setActiveTab}
        className="w-full"
      >
        <TabsList className="grid w-full md:w-auto grid-cols-2 md:flex">
          <TabsTrigger value="search">
            <Search className="h-4 w-4 mr-2" />
            Search Airports
          </TabsTrigger>
          <TabsTrigger 
            value="details" 
            disabled={!selectedAirport}
          >
            <MapPin className="h-4 w-4 mr-2" />
            {selectedAirport ? `${selectedAirport.icao} Details` : 'Airport Details'}
          </TabsTrigger>
          <TabsTrigger value="charts" disabled={!selectedAirport}>
            <FileText className="h-4 w-4 mr-2" />
            Charts
          </TabsTrigger>
          <TabsTrigger value="notams" disabled={!selectedAirport}>
            <AlertTriangle className="h-4 w-4 mr-2" />
            NOTAMs
          </TabsTrigger>
        </TabsList>
        
        <TabsContent value="search" className="mt-6">
          <Card>
            <CardHeader>
              <CardTitle>Search Airports</CardTitle>
              <CardDescription>
                {debouncedSearchQuery && debouncedSearchQuery.length >= 2 
                  ? `Search results for "${debouncedSearchQuery}"`
                  : 'Enter at least 2 characters to search'}
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isLoading ? (
                <div className="flex items-center justify-center py-12">
                  <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
                </div>
              ) : error ? (
                <div className="text-center py-12">
                  <p className="text-destructive">Error loading airports. Please try again.</p>
                </div>
              ) : airportsData?.data && airportsData.data.length > 0 ? (
                <div className="space-y-4">
                  <DataTable
                    columns={airportColumns(handleSelectAirport)}
                    data={airportsData.data}
                    pageSize={10}
                    pageCount={Math.ceil((airportsData.total || 0) / 10)}
                  />
                </div>
              ) : debouncedSearchQuery && debouncedSearchQuery.length >= 2 ? (
                <div className="text-center py-12">
                  <p className="text-muted-foreground">No airports found matching "{debouncedSearchQuery}"</p>
                </div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 py-6">
                  <QuickActionCard
                    icon={<MapPin className="h-6 w-6" />}
                    title="Major Airports"
                    description="Search for major airports in Brazil"
                    onClick={() => setSearchQuery('major')}
                  />
                  <QuickActionCard
                    icon={<Plane className="h-6 w-6" />}
                    title="International Airports"
                    description="Find international airports"
                    onClick={() => setSearchQuery('international')}
                  />
                  <QuickActionCard
                    icon={<Navigation className="h-6 w-6" />}
                    title="Near Me"
                    description="Find airports near your location"
                    onClick={() => {
                      if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition(
                          (position) => {
                            toast({
                              title: "Location detected",
                              description: "Searching for airports near you...",
                            })
                            // In a real app, you would use the coordinates to search for nearby airports
                            // For now, we'll just show a message
                            setSearchQuery('nearby')
                          },
                          (error) => {
                            toast({
                              title: "Location access denied",
                              description: "Please enable location services to find airports near you.",
                              variant: "destructive",
                            })
                          }
                        )
                      } else {
                        toast({
                          title: "Geolocation not supported",
                          description: "Your browser doesn't support geolocation.",
                          variant: "destructive",
                        })
                      }
                    }}
                  />
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>
        
        <TabsContent value="details" className="mt-6">
          {selectedAirport ? (
            <AirportDetailsView icao={selectedAirport.icao} />
          ) : (
            <Card>
              <CardHeader>
                <CardTitle>No Airport Selected</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Select an airport from the search results to view details.
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
        
        <TabsContent value="charts" className="mt-6">
          {selectedAirport ? (
            <Card>
              <CardHeader>
                <CardTitle>Charts for {selectedAirport.icao}</CardTitle>
                <CardDescription>
                  {selectedAirport.name}, {selectedAirport.city}, {selectedAirport.country}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="h-64 flex items-center justify-center bg-muted/50 rounded-lg">
                  <p className="text-muted-foreground">Charts will be displayed here</p>
                </div>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardHeader>
                <CardTitle>No Airport Selected</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Select an airport from the search results to view charts.
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
        
        <TabsContent value="notams" className="mt-6">
          {selectedAirport ? (
            <Card>
              <CardHeader>
                <CardTitle>NOTAMs for {selectedAirport.icao}</CardTitle>
                <CardDescription>
                  {selectedAirport.name}, {selectedAirport.city}, {selectedAirport.country}
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="h-64 flex items-center justify-center bg-muted/50 rounded-lg">
                  <p className="text-muted-foreground">NOTAMs will be displayed here</p>
                </div>
              </CardContent>
            </Card>
          ) : (
            <Card>
              <CardHeader>
                <CardTitle>No Airport Selected</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Select an airport from the search results to view NOTAMs.
                </p>
              </CardContent>
            </Card>
          )}
        </TabsContent>
      </Tabs>
    </div>
  )
}

// Helper component for quick action cards
interface QuickActionCardProps {
  icon: React.ReactNode
  title: string
  description: string
  onClick: () => void
}

function QuickActionCard({ icon, title, description, onClick }: QuickActionCardProps) {
  return (
    <button
      onClick={onClick}
      className="flex flex-col items-center justify-center p-6 border rounded-lg hover:bg-accent/50 transition-colors text-center"
    >
      <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center text-primary mb-3">
        {icon}
      </div>
      <h3 className="font-medium">{title}</h3>
      <p className="text-sm text-muted-foreground mt-1">{description}</p>
    </button>
  )
}
