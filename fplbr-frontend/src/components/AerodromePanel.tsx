import { useState } from 'react';
import { SunIcon } from '@heroicons/react/24/outline';
import type { AerodromoInfo } from '@/types';

type ChartType = 'TODAS' | 'VAC' | 'IAC' | 'SID' | 'STAR' | 'ROTA';

interface AerodromePanelProps {
  aerodrome: AerodromoInfo | null;
  title: string;
  isLoading?: boolean;
}

export default function AerodromePanel({ aerodrome, title, isLoading = false }: AerodromePanelProps) {
  const [selectedChartType, setSelectedChartType] = useState<ChartType>('TODAS');
  
  if (isLoading) {
    return (
      <div className="bg-white rounded-lg shadow p-4">
        <h3 className="text-lg font-semibold mb-4">{title}</h3>
        <div className="animate-pulse space-y-4">
          <div className="h-6 bg-gray-200 rounded w-1/3"></div>
          <div className="h-4 bg-gray-100 rounded w-1/2"></div>
          <div className="h-4 bg-gray-100 rounded w-2/3"></div>
        </div>
      </div>
    );
  }

  if (!aerodrome) {
    return (
      <div className="bg-white rounded-lg shadow p-4">
        <h3 className="text-lg font-semibold mb-4">{title}</h3>
        <div className="text-gray-500 text-center py-8">
          <p>Nenhum aeródromo selecionado</p>
        </div>
      </div>
    );
  }

  const { icao, nome, cartas, sun, coord, elev } = aerodrome;
  
  // Filter charts based on selected type
  const filteredCharts = selectedChartType === 'TODAS' 
    ? cartas 
    : cartas.filter(chart => chart.tipo === selectedChartType);

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="p-4 border-b border-gray-200">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-semibold">
            {title}: {icao} {nome && `- ${nome}`}
          </h3>
          {coord && (
            <span className="text-sm text-gray-500">
              {coord.lat > 0 ? 'N' : 'S'} {Math.abs(coord.lat).toFixed(4)}° 
              {coord.lon > 0 ? 'E' : 'W'} {Math.abs(coord.lon).toFixed(4)}°
              {elev !== undefined && ` • ${elev}ft`}
            </span>
          )}
        </div>
      </div>
      
      {/* Sunrise/Sunset Information */}
      {sun && (
        <div className="p-4 border-b border-gray-100 bg-blue-50">
          <div className="flex items-center text-sm text-gray-500">
            <SunIcon className="h-4 w-4 mr-1 text-gray-400" />
            {sun.sunset}
          </div>
          {sun.fonte && (
            <div className="mt-1 text-xs text-gray-400">Fonte: {sun.fonte}</div>
          )}
        </div>
      )}
      
      {/* Chart Filters */}
      <div className="p-3 bg-gray-50 border-b border-gray-200">
        <div className="flex flex-wrap gap-2">
          {(['TODAS', 'VAC', 'IAC', 'SID', 'STAR', 'ROTA'] as ChartType[]).map((type) => (
            <button
              key={type}
              type="button"
              onClick={() => setSelectedChartType(type)}
              className={`px-3 py-1 text-xs font-medium rounded-full ${
                selectedChartType === type
                  ? 'bg-blue-100 text-blue-800 border border-blue-200'
                  : 'bg-white text-gray-700 border border-gray-200 hover:bg-gray-50'
              }`}
            >
              {type}
            </button>
          ))}
        </div>
      </div>
      
      {/* Charts List */}
      <div className="divide-y divide-gray-200 max-h-96 overflow-y-auto">
        {filteredCharts.length > 0 ? (
          filteredCharts.map((carta, index) => (
            <div key={index} className="p-3 hover:bg-gray-50">
              <div className="flex justify-between items-center">
                <div>
                  <div className="font-medium text-gray-900">{carta.titulo}</div>
                  <div className="flex items-center text-sm text-gray-500 mt-1">
                    <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                      {carta.tipo}
                    </span>
                    {carta.versao && (
                      <span className="ml-2">Versão: {carta.versao}</span>
                    )}
                  </div>
                </div>
                <a
                  href={carta.pdf}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="inline-flex items-center px-3 py-1.5 border border-transparent text-xs font-medium rounded-md text-blue-700 bg-blue-100 hover:bg-blue-200 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
                >
                  <SunIcon className="h-4 w-4 mr-1 text-gray-400" />
                  Abrir Carta
                </a>
              </div>
            </div>
          ))
        ) : (
          <div className="p-4 text-center text-gray-500">
            Nenhuma carta encontrada para o filtro selecionado.
          </div>
        )}
      </div>
    </div>
  );
}
