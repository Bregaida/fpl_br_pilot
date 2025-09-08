import { useState, useMemo } from 'react';
import { ExclamationCircleIcon, ClockIcon } from '@heroicons/react/24/outline';

interface Notam {
  id: string;
  from?: string;
  to?: string;
  texto: string;
}

interface NotamPanelProps {
  notams: Record<string, Notam[]>;
  aerodromes: Array<{ icao: string; name?: string }>;
  isLoading?: boolean;
}

export default function NotamPanel({ notams, aerodromes, isLoading = false }: NotamPanelProps) {
  const [selectedAerodrome, setSelectedAerodrome] = useState<string>(
    aerodromes.length > 0 ? aerodromes[0].icao : ''
  );
  const [showActiveOnly, setShowActiveOnly] = useState(true);
  
  // Get NOTAMs for the selected aerodrome
  const aerodromeNotams = useMemo(() => {
    const aerodromeNotams = notams[selectedAerodrome] || [];
    
    if (!showActiveOnly) return aerodromeNotams;
    
    const now = new Date();
    return aerodromeNotams.filter(notam => {
      const from = notam.from ? new Date(notam.from) : null;
      const to = notam.to ? new Date(notam.to) : null;
      
      // If no dates, include the NOTAM
      if (!from && !to) return true;
      
      // Check if current time is within the NOTAM's active period
      const isAfterFrom = !from || now >= from;
      const isBeforeTo = !to || now <= to;
      
      return isAfterFrom && isBeforeTo;
    });
  }, [notams, selectedAerodrome, showActiveOnly]);

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'N/A';
    
    try {
      const date = new Date(dateString);
      return new Intl.DateTimeFormat('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        timeZone: 'UTC',
        hour12: false,
      }).format(date) + 'Z';
    } catch (e) {
      return dateString;
    }
  };

  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow p-4">
        <div className="animate-pulse space-y-4">
          <div className="h-6 bg-gray-200 rounded w-1/3"></div>
          <div className="h-4 bg-gray-100 rounded w-1/2"></div>
          <div className="h-4 bg-gray-100 rounded w-2/3"></div>
        </div>
      </div>
    );
  }

  if (aerodromes.length === 0) {
    return (
      <div className="bg-white rounded-lg shadow p-4">
        <div className="text-center py-4 text-gray-500">
          <p>Nenhum aerÃ³dromo selecionado</p>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="p-4 border-b border-gray-200">
        <h3 className="text-lg font-semibold">NOTAMs</h3>
      </div>
      
      {/* Aerodrome Selector and Filters */}
      <div className="px-4 py-3 bg-gray-50 border-b border-gray-200">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div className="flex-1 flex flex-wrap gap-2">
            {aerodromes.map(({ icao, name }) => (
              <button
                key={icao}
                type="button"
                onClick={() => setSelectedAerodrome(icao)}
                className={`px-3 py-1.5 text-sm font-medium rounded-md flex items-center ${
                  selectedAerodrome === icao
                    ? 'bg-blue-100 text-blue-800 border border-blue-200'
                    : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50'
                }`}
              >
                <ExclamationCircleIcon className="h-4 w-4 mr-1.5" />
                {icao} {name && `(${name})`}
              </button>
            ))}
          </div>
          
          <div className="flex items-center">
            <input
              id="active-only"
              type="checkbox"
              checked={showActiveOnly}
              onChange={(e) => setShowActiveOnly(e.target.checked)}
              className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
            />
            <label htmlFor="active-only" className="ml-2 text-sm text-gray-700">
              Mostrar apenas ativos
            </label>
          </div>
        </div>
      </div>
      
      {/* NOTAMs List */}
      <div className="divide-y divide-gray-200 max-h-96 overflow-y-auto">
        {aerodromeNotams.length > 0 ? (
          aerodromeNotams.map((notam) => (
            <div key={notam.id} className="p-4 hover:bg-gray-50">
              <div className="flex items-start">
                <div className="flex-shrink-0 pt-0.5">
                  <ExclamationCircleIcon className="h-5 w-5 text-yellow-500" />
                </div>
                <div className="ml-3 flex-1">
                  <div className="flex flex-col sm:flex-row sm:items-baseline justify-between">
                    <h4 className="text-sm font-medium text-gray-900">NOTAM {notam.id}</h4>
                    <div className="flex items-center text-xs text-gray-500">
                      <ClockIcon className="h-3.5 w-3.5 mr-1 text-gray-400" />
                      {formatDate(notam.from)} - {notam.to ? formatDate(notam.to) : 'PERM'}
                    </div>
                  </div>
                  <p className="mt-1 text-sm text-gray-700 whitespace-pre-line">{notam.texto}</p>
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="p-6 text-center">
            <ExclamationCircleIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">Nenhum NOTAM encontrado</h3>
            <p className="mt-1 text-sm text-gray-500">
              {showActiveOnly 
                ? 'Nenhum NOTAM ativo encontrado para o aerÃ³dromo selecionado.'
                : 'Nenhum NOTAM encontrado para o aerÃ³dromo selecionado.'}
            </p>
          </div>
        )}
      </div>
      
      {/* Footer with count */}
      <div className="px-4 py-2 bg-gray-50 border-t border-gray-200 text-right">
        <p className="text-xs text-gray-500">
          Exibindo {aerodromeNotams.length} NOTAMs
          {showActiveOnly && ' (apenas ativos)'}
        </p>
      </div>
    </div>
  );
}
