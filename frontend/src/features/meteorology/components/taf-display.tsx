import React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { Info, AlertTriangle } from 'lucide-react'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'
import { TafData } from '../types/metar-taf'
import { Badge } from '@/components/ui/badge'

interface TafDisplayProps {
  data?: TafData
  isLoading: boolean
  error?: Error | null
  icaoCode: string
}

const TafForecastItem = ({ forecast, index }: { forecast: any; index: number }) => {
  const formatWind = (wind: any) => {
    if (!wind) return 'Variable or light'
    if (wind.speed === 0) return 'Calm'
    
    let windStr = `${wind.degrees}°/${wind.speed}${wind.unit}`
    if (wind.gust) {
      windStr += `G${wind.gust}${wind.unit}`
    }
    
    return windStr
  }

  const formatVisibility = (visibility: any) => {
    if (!visibility) return 'No significant change'
    
    if (visibility.unit === 'M') {
      return `${visibility.value} meters`
    } else if (visibility.unit === 'KM') {
      return `${visibility.value} kilometers`
    } else {
      return `${visibility.value} statute miles`
    }
  }

  const formatClouds = (clouds: any[]) => {
    if (!clouds || clouds.length === 0) return 'Sky clear'
    
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

  const formatTime = (timeString: string) => {
    if (!timeString) return ''
    return new Date(timeString).toLocaleString()
  }

  const getForecastType = (type: string) => {
    switch (type) {
      case 'BECMG': return 'Becoming'
      case 'TEMPO': return 'Temporary'
      case 'PROB': return 'Probability'
      case 'FM': return 'From'
      default: return type
    }
  }

  return (
    <div key={index} className="border-l-4 border-primary pl-4 py-2 my-2">
      <div className="flex items-center gap-2">
        <span className="font-medium">{getForecastType(forecast.type)}</span>
        {forecast.probability && (
          <Badge variant="outline" className="text-xs">
            {forecast.probability}% prob
          </Badge>
        )}
        {forecast.from && forecast.to && (
          <span className="text-sm text-muted-foreground">
            {formatTime(forecast.from)} - {formatTime(forecast.to)}
          </span>
        )}
        {forecast.type === 'TEMPO' && (
          <Tooltip>
            <TooltipTrigger asChild>
              <Info className="h-4 w-4 text-muted-foreground" />
            </TooltipTrigger>
            <TooltipContent>
              <p>Temporary fluctuations expected during this period</p>
            </TooltipContent>
          </Tooltip>
        )}
      </div>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-2 mt-2 text-sm">
        <div className="space-y-1">
          {forecast.wind && (
            <div className="flex items-center">
              <span className="font-medium w-20">Wind:</span>
              <span>{formatWind(forecast.wind)}</span>
            </div>
          )}
          {forecast.visibility && (
            <div className="flex items-center">
              <span className="font-medium w-20">Visibility:</span>
              <span>{formatVisibility(forecast.visibility)}</span>
            </div>
          )}
        </div>
        
        <div className="space-y-1">
          {forecast.clouds && forecast.clouds.length > 0 && (
            <div className="flex items-start">
              <span className="font-medium w-20">Clouds:</span>
              <span>{formatClouds(forecast.clouds)}</span>
            </div>
          )}
          {forecast.weather && forecast.weather.length > 0 && (
            <div className="flex items-start">
              <span className="font-medium w-20">Weather:</span>
              <span>{forecast.weather.join(', ')}</span>
            </div>
          )}
        </div>
      </div>
      
      {forecast.windShear && (
        <div className="mt-2 p-2 bg-amber-50 dark:bg-amber-900/20 rounded-md text-sm flex items-start gap-2">
          <AlertTriangle className="h-4 w-4 mt-0.5 text-amber-600 dark:text-amber-400 flex-shrink-0" />
          <div>
            <div className="font-medium">Wind Shear Warning</div>
            <div>At {forecast.windShear.height}ft: {forecast.windShear.degrees}°/{forecast.windShear.speed}kt</div>
          </div>
        </div>
      )}
    </div>
  )
}

export function TafDisplay({ data, isLoading, error, icaoCode }: TafDisplayProps) {
  if (isLoading) {
    return (
      <Card className="w-full">
        <CardHeader>
          <CardTitle>TAF - {icaoCode}</CardTitle>
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
          <CardTitle>Error loading TAF for {icaoCode}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-sm text-muted-foreground">
            {error?.message || 'No TAF data available for this location'}
          </p>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className="w-full">
      <CardHeader className="pb-2">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg">
            TAF - {data.station}
          </CardTitle>
          <div className="text-sm text-muted-foreground">
            Valid from {new Date(data.validFrom).toLocaleString()} to {new Date(data.validTo).toLocaleString()}
          </div>
        </div>
        <div className="text-sm text-muted-foreground">
          Issued: {new Date(data.issueTime).toLocaleString()}
        </div>
      </CardHeader>
      <CardContent>
        <div className="space-y-2">
          <div className="font-mono text-sm bg-muted/50 p-2 rounded">
            {data.raw}
          </div>
          
          <div className="mt-4 space-y-4">
            {data.forecast.map((forecast, index) => (
              <TafForecastItem key={index} forecast={forecast} index={index} />
            ))}
          </div>
          
          <div className="mt-4 text-xs text-muted-foreground">
            <p>TAF forecasts are typically issued every 6 hours and are valid for 24-30 hours.</p>
            <p>Always check for the latest updates before your flight.</p>
          </div>
        </div>
      </CardContent>
    </Card>
  )
}
