import React from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { AlertTriangle, Info } from 'lucide-react'
import { Badge } from '@/components/ui/badge'
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip'

type AlertType = 'SIGMET' | 'SIGMET' | 'AIRMET' | 'GAMET' | 'NOTAM'

interface SafetyAlertProps {
  type: AlertType
  id: string
  validFrom: string
  validTo: string
  fir: string
  description: string
  severity?: 'info' | 'warning' | 'danger'
  className?: string
}

export const SafetyAlert: React.FC<SafetyAlertProps> = ({
  type,
  id,
  validFrom,
  validTo,
  fir,
  description,
  severity = 'info',
  className = '',
}) => {
  const getAlertStyles = () => {
    switch (severity) {
      case 'warning':
        return 'border-amber-500/30 bg-amber-50 dark:bg-amber-900/10'
      case 'danger':
        return 'border-red-500/30 bg-red-50 dark:bg-red-900/10'
      case 'info':
      default:
        return 'border-blue-500/30 bg-blue-50 dark:bg-blue-900/10'
    }
  }

  const getTypeBadge = () => {
    switch (type) {
      case 'SIGMET':
        return <Badge variant="destructive">SIGMET</Badge>
      case 'AIRMET':
        return <Badge variant="warning">AIRMET</Badge>
      case 'GAMET':
        return <Badge variant="info">GAMET</Badge>
      case 'NOTAM':
        return <Badge>NOTAM</Badge>
      default:
        return <Badge variant="outline">{type}</Badge>
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString()
  }

  return (
    <div className={`border-l-4 rounded-r-md p-4 mb-4 ${getAlertStyles()} ${className}`}>
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-2">
          {getTypeBadge()}
          <span className="text-sm font-medium">{id}</span>
        </div>
        <div className="text-xs text-muted-foreground">
          FIR: {fir}
        </div>
      </div>
      
      <div className="mt-2 text-sm">
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <span>Valid: {formatDate(validFrom)}</span>
          <span>to</span>
          <span>{formatDate(validTo)}</span>
        </div>
        
        <div className="mt-2">
          <p className="whitespace-pre-line">{description}</p>
        </div>
      </div>
    </div>
  )
}

interface SafetyAlertsProps {
  title: string
  type: AlertType
  alerts: Array<{
    id: string
    validFrom: string
    validTo: string
    fir: string
    description: string
  }>
  isLoading: boolean
  error?: Error | null
  emptyMessage?: string
}

export const SafetyAlerts: React.FC<SafetyAlertsProps> = ({
  title,
  type,
  alerts,
  isLoading,
  error,
  emptyMessage = 'No active alerts',
}) => {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="space-y-2">
                <Skeleton className="h-6 w-48" />
                <Skeleton className="h-4 w-full" />
                <Skeleton className="h-4 w-3/4" />
              </div>
            ))}
          </div>
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card className="border-destructive">
        <CardHeader className="text-destructive">
          <CardTitle>Error loading {title.toLowerCase()}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">
            {error.message || 'An error occurred while loading the data'}
          </p>
        </CardContent>
      </Card>
    )
  }

  if (alerts.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>{title}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8 text-muted-foreground">
            {emptyMessage}
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {alerts.map((alert, index) => (
          <SafetyAlert
            key={`${type}-${alert.id}-${index}`}
            type={type}
            {...alert}
            severity={
              type === 'SIGMET' ? 'danger' : 
              type === 'AIRMET' ? 'warning' : 'info'
            }
          />
        ))}
      </CardContent>
    </Card>
  )
}

// Helper components for specific alert types
export const SigmetAlerts: React.FC<Omit<SafetyAlertsProps, 'type' | 'title'>> = (props) => (
  <SafetyAlerts 
    type="SIGMET" 
    title="SIGMETs"
    emptyMessage="No active SIGMETs"
    {...props} 
  />
)

export const AirmetAlerts: React.FC<Omit<SafetyAlertsProps, 'type' | 'title'>> = (props) => (
  <SafetyAlerts 
    type="AIRMET" 
    title="AIRMETs"
    emptyMessage="No active AIRMETs"
    {...props} 
  />
)

export const NotamAlerts: React.FC<Omit<SafetyAlertsProps, 'type' | 'title'>> = (props) => (
  <SafetyAlerts 
    type="NOTAM" 
    title="NOTAMs"
    emptyMessage="No active NOTAMs"
    {...props} 
  />
)
