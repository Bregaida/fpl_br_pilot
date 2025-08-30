import React from 'react'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { useAirportAllData } from '../hooks/use-airports-queries'
import { Skeleton } from '@/components/ui/skeleton'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { AlertTriangle, Info, MapPin, Radio, Ruler, Waves } from 'lucide-react'
import { Badge } from '@/components/ui/badge'

interface AirportDetailsViewProps {
  icao: string
}

export function AirportDetailsView({ icao }: AirportDetailsViewProps) {
  const { 
    details, 
    runways, 
    frequencies, 
    notams, 
    charts, 
    isLoading, 
    isError, 
    error 
  } = useAirportAllData(icao)

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-10 w-1/3" />
        <Skeleton className="h-20 w-full" />
        <Skeleton className="h-64 w-full" />
      </div>
    )
  }

  if (isError || !details) {
    return (
      <Alert variant="destructive">
        <AlertTriangle className="h-4 w-4" />
        <AlertTitle>Error loading airport details</AlertTitle>
        <AlertDescription>
          {error?.message || 'An error occurred while loading airport information'}
        </AlertDescription>
      </Alert>
    )
  }

  return (
    <div className="space-y-6">
      {/* Airport Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <div>
          <h2 className="text-2xl font-bold">
            {details.name} ({details.icao}{details.iata ? ` / ${details.iata}` : ''})
          </h2>
          <p className="text-muted-foreground flex items-center">
            <MapPin className="h-4 w-4 mr-1" />
            {details.city}, {details.municipality ? `${details.municipality}, ` : ''}{details.country}
          </p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Badge variant="outline" className="text-sm">
            Elevation: {details.elevation} ft
          </Badge>
          <Badge variant="outline" className="text-sm">
            Timezone: {details.timezone.replace('_', ' ')}
          </Badge>
          <Badge variant="outline" className="text-sm">
            {details.type === 'large_airport' ? 'Large Airport' : 
             details.type === 'medium_airport' ? 'Medium Airport' : 
             details.type === 'small_airport' ? 'Small Airport' : 
             details.type === 'heliport' ? 'Heliport' : 'Airport'}
          </Badge>
        </div>
      </div>

      <Tabs defaultValue="overview" className="w-full">
        <TabsList className="grid w-full md:w-auto grid-cols-2 md:flex">
          <TabsTrigger value="overview">Overview</TabsTrigger>
          <TabsTrigger value="runways">Runways ({runways?.length || 0})</TabsTrigger>
          <TabsTrigger value="frequencies">Frequencies ({frequencies?.length || 0})</TabsTrigger>
          <TabsTrigger value="notams">NOTAMs ({notams?.length || 0})</TabsTrigger>
          <TabsTrigger value="charts">Charts ({charts?.length || 0})</TabsTrigger>
        </TabsList>

        <TabsContent value="overview" className="mt-6 space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle className="text-lg">Airport Information</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="space-y-2">
                  <h3 className="font-medium">Location</h3>
                  <div className="text-sm text-muted-foreground space-y-1">
                    <p>Latitude: {details.latitude.toFixed(6)}°</p>
                    <p>Longitude: {details.longitude.toFixed(6)}°</p>
                    <p>Elevation: {details.elevation} ft</p>
                    <p>Continent: {details.continent}</p>
                    <p>Timezone: {details.timezone.replace('_', ' ')}</p>
                  </div>
                </div>

                <div className="space-y-2">
                  <h3 className="font-medium">Details</h3>
                  <div className="text-sm text-muted-foreground space-y-1">
                    <p>Type: {details.type.replace('_', ' ')}</p>
                    <p>Scheduled Service: {details.scheduledService ? 'Yes' : 'No'}</p>
                    <p>GPS Code: {details.gpsCode || 'N/A'}</p>
                    <p>Local Code: {details.localCode || 'N/A'}</p>
                  </div>
                </div>

                {(details.homeLink || details.wikipediaLink) && (
                  <div className="space-y-2">
                    <h3 className="font-medium">Links</h3>
                    <div className="text-sm space-y-1">
                      {details.homeLink && (
                        <p>
                          <a 
                            href={details.homeLink} 
                            target="_blank" 
                            rel="noopener noreferrer"
                            className="text-primary hover:underline"
                          >
                            Official Website
                          </a>
                        </p>
                      )}
                      {details.wikipediaLink && (
                        <p>
                          <a 
                            href={details.wikipediaLink} 
                            target="_blank" 
                            rel="noopener noreferrer"
                            className="text-primary hover:underline"
                          >
                            Wikipedia
                          </a>
                        </p>
                      )}
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            <div className="space-y-6">
              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-lg flex items-center gap-2">
                    <Waves className="h-5 w-5" />
                    Weather
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="h-40 flex items-center justify-center bg-muted/30 rounded-lg">
                    <p className="text-muted-foreground">Weather information will be displayed here</p>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardHeader className="pb-2">
                  <CardTitle className="text-lg flex items-center gap-2">
                    <MapPin className="h-5 w-5" />
                    Location
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="h-64 bg-muted/30 rounded-lg flex items-center justify-center">
                    <p className="text-muted-foreground">Map will be displayed here</p>
                  </div>
                </CardContent>
              </Card>
            </div>
          </div>
        </TabsContent>

        <TabsContent value="runways" className="mt-6">
          {runways && runways.length > 0 ? (
            <div className="space-y-4">
              {runways.map((runway) => (
                <Card key={runway.id}>
                  <CardHeader className="pb-2">
                    <div className="flex items-center justify-between">
                      <CardTitle className="text-lg">
                        {runway.leIdent}/{runway.heIdent}
                      </CardTitle>
                      <Badge variant="outline" className="text-sm">
                        {runway.lengthFt} ft × {runway.widthFt} ft
                      </Badge>
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                      <div>
                        <h4 className="font-medium mb-2">Runway {runway.leIdent}</h4>
                        <div className="space-y-1 text-sm">
                          <p>Heading: {runway.leHeadingDegT}°</p>
                          <p>Elevation: {runway.leElevationFt} ft</p>
                          <p>Displaced Threshold: {runway.leDisplacedThresholdFt || 0} ft</p>
                          <p>Surface: {runway.surface}</p>
                        </div>
                      </div>
                      <div>
                        <h4 className="font-medium mb-2">Runway {runway.heIdent}</h4>
                        <div className="space-y-1 text-sm">
                          <p>Heading: {runway.heHeadingDegT}°</p>
                          <p>Elevation: {runway.heElevationFt} ft</p>
                          <p>Displaced Threshold: {runway.heDisplacedThresholdFt || 0} ft</p>
                          <p>Lighted: {runway.lighted ? 'Yes' : 'No'}</p>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Alert>
              <Info className="h-4 w-4" />
              <AlertTitle>No runway information available</AlertTitle>
              <AlertDescription>
                There is no runway data available for this airport.
              </AlertDescription>
            </Alert>
          )}
        </TabsContent>

        <TabsContent value="frequencies" className="mt-6">
          {frequencies && frequencies.length > 0 ? (
            <div className="space-y-4">
              <Card>
                <CardContent className="p-0">
                  <div className="overflow-x-auto">
                    <table className="w-full">
                      <thead>
                        <tr className="border-b">
                          <th className="text-left p-3 font-medium">Type</th>
                          <th className="text-left p-3 font-medium">Description</th>
                          <th className="text-right p-3 font-medium">Frequency (MHz)</th>
                        </tr>
                      </thead>
                      <tbody>
                        {frequencies.map((freq) => (
                          <tr key={freq.id} className="border-b hover:bg-muted/50">
                            <td className="p-3">
                              <Badge variant="outline">
                                {freq.type}
                              </Badge>
                            </td>
                            <td className="p-3">{freq.description}</td>
                            <td className="p-3 text-right font-mono">
                              {(freq.frequencyMhz).toFixed(3)}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </CardContent>
              </Card>
            </div>
          ) : (
            <Alert>
              <Info className="h-4 w-4" />
              <AlertTitle>No frequency information available</AlertTitle>
              <AlertDescription>
                There is no frequency data available for this airport.
              </AlertDescription>
            </Alert>
          )}
        </TabsContent>

        <TabsContent value="notams" className="mt-6">
          {notams && notams.length > 0 ? (
            <div className="space-y-4">
              {notams.map((notam) => (
                <Card key={notam.id} className="border-l-4 border-amber-500">
                  <CardHeader className="pb-2">
                    <div className="flex items-center justify-between">
                      <CardTitle className="text-lg">
                        {notam.notamNumber}
                      </CardTitle>
                      <Badge variant="outline" className="text-amber-600">
                        {notam.type}
                      </Badge>
                    </div>
                    <div className="text-sm text-muted-foreground">
                      Valid: {new Date(notam.validFrom).toLocaleString()} - {new Date(notam.validUntil).toLocaleString()}
                    </div>
                  </CardHeader>
                  <CardContent>
                    <div className="whitespace-pre-line text-sm">
                      {notam.body}
                    </div>
                    {notam.lowerLimit && notam.upperLimit && (
                      <div className="mt-2 text-sm text-muted-foreground">
                        Affected Altitude: {notam.lowerLimit} - {notam.upperLimit}
                      </div>
                    )}
                  </CardContent>
                </Card>
              ))}
            </div>
          ) : (
            <Alert>
              <Info className="h-4 w-4" />
              <AlertTitle>No active NOTAMs</AlertTitle>
              <AlertDescription>
                There are no active NOTAMs for this airport.
              </AlertDescription>
            </Alert>
          )}
        </TabsContent>

        <TabsContent value="charts" className="mt-6">
          {charts && charts.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {charts.map((chart) => (
                <Card key={chart.id} className="overflow-hidden hover:shadow-md transition-shadow">
                  <div className="h-40 bg-muted/50 flex items-center justify-center">
                    <div className="text-center p-4">
                      <FileText className="h-10 w-10 mx-auto text-muted-foreground mb-2" />
                      <p className="font-medium">{chart.chartName}</p>
                      <p className="text-sm text-muted-foreground">{chart.section}</p>
                    </div>
                  </div>
                  <div className="p-4">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-muted-foreground">
                        {chart.fileSize} • {chart.fileExtension.toUpperCase()}
                      </span>
                      <Button variant="outline" size="sm" asChild>
                        <a 
                          href={chart.fileUrl} 
                          target="_blank" 
                          rel="noopener noreferrer"
                        >
                          View
                        </a>
                      </Button>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          ) : (
            <Alert>
              <Info className="h-4 w-4" />
              <AlertTitle>No charts available</AlertTitle>
              <AlertDescription>
                There are no charts available for this airport.
              </AlertDescription>
            </Alert>
          )}
        </TabsContent>
      </Tabs>
    </div>
  )
}
