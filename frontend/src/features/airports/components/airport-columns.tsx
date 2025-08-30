import type { ColumnDef, Row } from "@tanstack/react-table"
import type { Airport } from "../types/airport"
import { Button } from "@/components/ui/button"
import { ArrowUpRight, MapPin } from "lucide-react"

export const columns = (onSelect: (airport: Airport) => void): ColumnDef<Airport>[] => [
  {
    accessorKey: "icao",
    header: "ICAO",
    cell: ({ row }: { row: Row<Airport> }) => (
      <div className="font-mono font-medium">
        {row.original.icao}
      </div>
    ),
  },
  {
    accessorKey: "iata",
    header: "IATA",
    cell: ({ row }: { row: { original: Airport } }) => (
      <div className="font-mono text-sm">
        {row.original.iata || '-'}
      </div>
    ),
  },
  {
    accessorKey: "name",
    header: "Name",
    cell: ({ row }: { row: Row<Airport> }) => (
      <div className="font-medium">
        {row.original.name}
        <div className="text-sm text-muted-foreground flex items-center">
          <MapPin className="h-3.5 w-3.5 mr-1" />
          {row.original.city}, {row.original.country}
        </div>
      </div>
    ),
  },
  {
    accessorKey: "type",
    header: "Type",
    cell: ({ row }: { row: Row<Airport> }) => {
      const type = row.original.type
      const typeMap: Record<string, string> = {
        'large_airport': 'Large Airport',
        'medium_airport': 'Medium Airport',
        'small_airport': 'Small Airport',
        'heliport': 'Heliport',
        'closed': 'Closed',
      }
      
      return (
        <div className="text-sm">
          {typeMap[type] || type}
        </div>
      )
    },
  },
  {
    accessorKey: "elevation",
    header: "Elevation",
    cell: ({ row }: { row: Row<Airport> }) => (
      <div className="text-sm">
        {row.original.elevation ? `${row.original.elevation} ft` : '-'}
      </div>
    ),
  },
  {
    id: "actions",
    cell: ({ row }: { row: Row<Airport> }) => (
      <div className="flex justify-end">
        <Button
          variant="ghost"
          size="sm"
          className="h-8"
          onClick={() => onSelect(row.original)}
        >
          <span className="sr-only">View details</span>
          <ArrowUpRight className="h-4 w-4" />
        </Button>
      </div>
    ),
  },
]
