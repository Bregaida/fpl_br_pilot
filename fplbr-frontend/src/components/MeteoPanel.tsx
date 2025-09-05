import { useState } from 'react';
import { CloudIcon, ExclamationTriangleIcon } from '@heroicons/react/24/outline'; // Removed unused SunIcon and ArrowPathIcon

type MeteoData = {
  raw: {
    metar?: string;
    taf?: string;
    updatedAt?: string;
  };
  decoded: {
    metar?: any;
    taf?: any;
  };
};

interface MeteoPanelProps {
  meteoData: Record<string, MeteoData>;
  aerodromes: Array<{ icao: string; name?: string }>;
  isLoading?: boolean;
}

export default function MeteoPanel({ meteoData, aerodromes, isLoading = false }: MeteoPanelProps) {
  const [selectedAerodrome, setSelectedAerodrome] = useState<string>(
    aerodromes.length > 0 ? aerodromes[0].icao : ''
  );
  const [activeTab, setActiveTab] = useState<'raw' | 'decoded'>('raw');

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
          <p>Nenhum aeródromo selecionado</p>
        </div>
      </div>
    );
  }

  const aerodrome = aerodromes.find(a => a.icao === selectedAerodrome) || aerodromes[0];
  const data = meteoData[aerodrome.icao];
  const updatedAt = data?.raw?.updatedAt ? new Date(data.raw.updatedAt) : null;

  const renderMetar = () => {
    if (!data) return <p className="text-gray-500 italic">Dados meteorológicos não disponíveis</p>;
    
    if (activeTab === 'raw') {
      return (
        <div className="space-y-4">
          <div>
            <h4 className="font-medium text-gray-700 mb-1">METAR</h4>
            <pre className="bg-gray-50 p-3 rounded text-sm font-mono whitespace-pre-wrap">
              {data.raw.metar || 'Nenhum METAR disponível'}
            </pre>
          </div>
          <div>
            <h4 className="font-medium text-gray-700 mb-1">TAF</h4>
            <pre className="bg-gray-50 p-3 rounded text-sm font-mono whitespace-pre-wrap">
              {data.raw.taf || 'Nenhum TAF disponível'}
            </pre>
          </div>
        </div>
      );
    } else {
      return (
        <div className="space-y-6">
          <div>
            <h4 className="font-medium text-gray-700 mb-2">METAR Decodificado</h4>
            {data.decoded.metar ? (
              <div className="bg-gray-50 p-3 rounded text-sm">
                <pre className="whitespace-pre-wrap">
                  {JSON.stringify(data.decoded.metar, null, 2)}
                </pre>
              </div>
            ) : (
              <p className="text-gray-500 italic">Não foi possível decodificar o METAR</p>
            )}
          </div>
          <div>
            <h4 className="font-medium text-gray-700 mb-2">TAF Decodificado</h4>
            {data.decoded.taf ? (
              <div className="bg-gray-50 p-3 rounded text-sm">
                <pre className="whitespace-pre-wrap">
                  {JSON.stringify(data.decoded.taf, null, 2)}
                </pre>
              </div>
            ) : (
              <p className="text-gray-500 italic">Não foi possível decodificar o TAF</p>
            )}
          </div>
        </div>
      );
    }
  };

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="p-4 border-b border-gray-200">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-semibold">Meteorologia</h3>
          {updatedAt && (
            <div className="flex items-center text-sm text-gray-500">
              <CloudIcon className="h-4 w-4 mr-1 text-yellow-400" />
              Atualizado: {updatedAt.toLocaleTimeString()}
            </div>
          )}
        </div>
      </div>
      
      {/* Aerodrome Selector */}
      <div className="px-4 py-3 bg-gray-50 border-b border-gray-200">
        <div className="flex flex-wrap gap-2">
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
              <CloudIcon className="h-4 w-4 mr-1.5" />
              {icao} {name && `(${name})`}
            </button>
          ))}
        </div>
      </div>
      
      {/* Tabs */}
      <div className="border-b border-gray-200">
        <nav className="-mb-px flex">
          <button
            type="button"
            onClick={() => setActiveTab('raw')}
            className={`py-3 px-4 text-sm font-medium border-b-2 ${
              activeTab === 'raw'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Dados Brutos
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('decoded')}
            className={`py-3 px-4 text-sm font-medium border-b-2 ${
              activeTab === 'decoded'
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            Dados Decodificados
          </button>
        </nav>
      </div>
      
      {/* Content */}
      <div className="p-4">
        {data ? (
          renderMetar()
        ) : (
          <div className="text-center py-6">
            <ExclamationTriangleIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-sm font-medium text-gray-900">Dados não disponíveis</h3>
            <p className="mt-1 text-sm text-gray-500">
              Não foi possível carregar as informações meteorológicas para {selectedAerodrome}.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
