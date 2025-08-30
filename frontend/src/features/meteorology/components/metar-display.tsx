import React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { Info } from 'lucide-react'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { MetarData } from '../types/metar-taf'

interface MetarDisplayProps {
  data?: MetarData
  isLoading: boolean
  error?: Error | null
  icaoCode: string
}

export function MetarDisplay({ data, isLoading, error, icaoCode }: MetarDisplayProps) {
  if (isLoading) {
    return (
      <Card className="w-full">
        <CardHeader>
          <CardTitle>METAR - {icaoCode}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-2">
            <Skeleton className="h-6 w-full" />
            <Skeleton className="h-4 w-3/4" />
            <Skeleton className="h-4 w-1/2" />
          </div>
        </CardContent>
      </Card>
    )
  }

  if (error || !data) {
    return (
      <Card className="w-full border-destructive">
        <CardHeader className="text-destructive">
          <CardTitle>Error loading METAR for {icaoCode}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">
            {error?.message || 'No data available'}
          </p>
        </CardContent>
      </Card>
    )
  }

  const formatWind = (wind: MetarData['wind']) => {
    if (wind.speed === 0) return 'Calm'
    
    let windStr = `${wind.degrees}°/${wind.speed}${wind.unit}`
    if (wind.gust) {
      windStr += `G${wind.gust}${wind.unit}`
    }
    
    return windStr
  }

  const formatVisibility = (visibility: MetarData['visibility']) => {
    if (visibility.unit === 'M') {
      return `${visibility.value} meters`
    } else if (visibility.unit === 'KM') {
      return `${visibility.value} kilometers`
    } else {
      return `${visibility.value} statute miles`
    }
  }

  const formatClouds = (clouds: MetarData['clouds']) => {
    if (clouds.length === 0) return 'Sky clear'
    
    return clouds.map(cloud => {
      let cloudStr = ''
      if (cloud.type === 'FEW' || cloud.type === 'SCT' || cloud.type === 'BKN' || cloud.type === 'OVC') {
        cloudStr += `${cloud.type}${cloud.altitude / 100} `
      } else {
        cloudStr += `${cloud.type} `
      }
      
      if (cloud.modifier) {
        cloudStr += cloud.modifier
      }
      
      return cloudStr.trim()
    }).join(', ')
  }

  return (
    <Card className="w-full">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg">
            METAR - {data.station}
          </CardTitle>
          <div className="text-sm text-muted-foreground">
            {new Date(data.time).toLocaleString()}
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="font-mono text-sm bg-muted/50 p-2 rounded">
            {data.raw}
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 pt-2">
            <div className="space-y-2">
              <div className="flex items-center">
                <span className="font-medium w-24">Wind:</span>
                <span>{formatWind(data.wind)}</span>
              </div>
              <div className="flex items-center">
                <span className="font-medium w-24">Visibility:</span>
                <span>{formatVisibility(data.visibility)}</span>
              </div>
              <div className="flex items-center">
                <span className="font-medium w-24">Clouds:</span>
                <span>{formatClouds(data.clouds)}</span>
              </div>
            </div>
            
            <div className="space-y-2">
              <div className="flex items-center">
                <span className="font-medium w-24">Temp:</span>
                <span>{data.temperature}°C</span>
              </div>
              <div className="flex items-center">
                <span className="font-medium w-24">Dew Point:</span>
                <span>{data.dewpoint}°C</span>
              </div>
              <div className="flex items-center">
                <span className="font-medium w-24">QNH:</span>
                <span>{data.qnh} hPa</span>
                {data.qnh < 1013 ? (
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <Info className="h-4 w-4 ml-2 text-amber-500" />
                    </TooltipTrigger>
                    <TooltipContent>
                      <p>QNH is below standard (1013 hPa)</p>
                    </TooltipContent>
                  </Tooltip>
                ) : null}
              </div>
            </div>
          </div>
          
          {data.weather.length > 0 && (
            <div className="pt-2">
              <div className="font-medium">Weather:</div>
              <div className="text-sm text-muted-foreground">
                {data.weather.join(', ')}
              </div>
            </div>
          )}
          
          {data.trend && (
            <div className="pt-2">
              <div className="font-medium">Trend:</div>
              <div className="text-sm text-muted-foreground">
                {data.trend}
              </div>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  )
}
