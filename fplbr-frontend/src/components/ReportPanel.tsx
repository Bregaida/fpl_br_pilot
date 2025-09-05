import { useRef } from 'react';
import { DocumentTextIcon } from '@heroicons/react/24/outline'; // Removed unused ArrowDownTrayIcon
import { FplComposedResponse } from '@/types';

interface ReportPanelProps {
  data: FplComposedResponse | null;
  isLoading?: boolean;
}

export default function ReportPanel({ data, isLoading = false }: ReportPanelProps) {
  const reportRef = useRef<HTMLDivElement>(null);

  const handleExportPDF = () => {
    // TODO: Implement PDF export functionality
    alert('Exportar para PDF será implementado em breve!');
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

  if (!data) {
    return (
      <div className="bg-white rounded-lg shadow p-6 text-center">
        <DocumentTextIcon className="mx-auto h-12 w-12 text-gray-400" />
        <h3 className="mt-2 text-sm font-medium text-gray-900">Nenhum relatório disponível</h3>
        <p className="mt-1 text-sm text-gray-500">Preencha o formulário para gerar o relatório.</p>
      </div>
    );
  }

  const { form, origem, destino, notams, atsPreview } = data;

  return (
    <div className="bg-white rounded-lg shadow overflow-hidden">
      <div className="p-4 border-b border-gray-200">
        <div className="flex justify-between items-center">
          <h3 className="text-lg font-semibold">Relatório Consolidado</h3>
          <button
            onClick={handleExportPDF}
            className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            <DocumentTextIcon className="h-4 w-4 mr-1" aria-hidden="true" />
            Exportar PDF
          </button>
        </div>
      </div>
      
      <div ref={reportRef} className="p-6 space-y-8">
        {/* Flight Plan Summary */}
        <div>
          <h4 className="text-lg font-medium text-gray-900 mb-4 pb-2 border-b border-gray-200">
            Resumo do Plano de Voo
          </h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h5 className="font-medium text-gray-700 mb-2">Informações da Aeronave</h5>
              <dl className="space-y-2">
                <div className="sm:grid sm:grid-cols-3 sm:gap-4">
                  <dt className="text-sm font-medium text-gray-500">Modo</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:col-span-2">{form.modo}</dd>
                </div>
                <div className="sm:grid sm:grid-cols-3 sm:gap-4">
                  <dt className="text-sm font-medium text-gray-500">Aeronave</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:col-span-2">
                    {form.item7.identificacaoAeronave}
                    {form.item7.indicativoChamada && ' (Indicativo de Chamada)'}
                  </dd>
                </div>
                <div className="sm:grid sm:grid-cols-3 sm:gap-4">
                  <dt className="text-sm font-medium text-gray-500">Tipo</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:col-span-2">
                    {form.item9.tipoAeronave} (Cat. Turbulência: {form.item9.catTurbulencia})
                  </dd>
                </div>
              </dl>
            </div>
            
            <div>
              <h5 className="font-medium text-gray-700 mb-2">Plano de Voo</h5>
              <dl className="space-y-2">
                <div className="sm:grid sm:grid-cols-3 sm:gap-4">
                  <dt className="text-sm font-medium text-gray-500">De/Para</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:col-span-2">
                    {form.item13.aerodromoPartida} → {form.item16.aerodromoDestino}
                  </dd>
                </div>
                <div className="sm:grid sm:grid-cols-3 sm:gap-4">
                  <dt className="text-sm font-medium text-gray-500">Data/Hora</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:col-span-2">
                    {form.item18.dof} às {form.item13.hora}Z
                  </dd>
                </div>
                <div className="sm:grid sm:grid-cols-3 sm:gap-4">
                  <dt className="text-sm font-medium text-gray-500">Rota/Nível</dt>
                  <dd className="mt-1 text-sm text-gray-900 sm:col-span-2">
                    {form.item15.rota} a {form.item15.nivel} ({form.item15.velocidadeCruzeiro})
                  </dd>
                </div>
              </dl>
            </div>
          </div>
        </div>
        
        {/* Aerodromes Information */}
        <div>
          <h4 className="text-lg font-medium text-gray-900 mb-4 pb-2 border-b border-gray-200">
            Informações dos Aeródromos
          </h4>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h5 className="font-medium text-gray-700 mb-2">Origem: {origem.icao}</h5>
              {origem.nome && <p className="text-sm text-gray-900">{origem.nome}</p>}
              {origem.coord && (
                <p className="text-xs text-gray-500 mt-1">
                  Coord: {origem.coord.lat > 0 ? 'N' : 'S'} {Math.abs(origem.coord.lat).toFixed(4)}° 
                  {origem.coord.lon > 0 ? 'E' : 'W'} {Math.abs(origem.coord.lon).toFixed(4)}°
                  {origem.elev !== undefined && ` • ${origem.elev}ft`}
                </p>
              )}
              {origem.sun && (
                <div className="mt-2 text-sm">
                  <p>Nascer do sol: {origem.sun.sunrise}</p>
                  <p>Pôr do sol: {origem.sun.sunset}</p>
                </div>
              )}
            </div>
            
            <div>
              <h5 className="font-medium text-gray-700 mb-2">Destino: {destino.icao}</h5>
              {destino.nome && <p className="text-sm text-gray-900">{destino.nome}</p>}
              {destino.coord && (
                <p className="text-xs text-gray-500 mt-1">
                  Coord: {destino.coord.lat > 0 ? 'N' : 'S'} {Math.abs(destino.coord.lat).toFixed(4)}° 
                  {destino.coord.lon > 0 ? 'E' : 'W'} {Math.abs(destino.coord.lon).toFixed(4)}°
                  {destino.elev !== undefined && ` • ${destino.elev}ft`}
                </p>
              )}
              {destino.sun && (
                <div className="mt-2 text-sm">
                  <p>Nascer do sol: {destino.sun.sunrise}</p>
                  <p>Pôr do sol: {destino.sun.sunset}</p>
                </div>
              )}
            </div>
          </div>
        </div>
        
        {/* NOTAMs */}
        <div>
          <h4 className="text-lg font-medium text-gray-900 mb-4 pb-2 border-b border-gray-200">
            NOTAMs Relevantes
          </h4>
          <div className="space-y-4">
            {Object.entries(notams).map(([icao, icaoNotams]) => (
              <div key={icao} className="border border-gray-200 rounded-lg p-4">
                <h5 className="font-medium text-gray-900 mb-2">{icao}</h5>
                {icaoNotams.length > 0 ? (
                  <ul className="space-y-3">
                    {icaoNotams.map((notam) => (
                      <li key={notam.id} className="text-sm">
                        <div className="text-gray-500 text-xs mb-1">
                          {notam.from} - {notam.to || 'PERM'}
                        </div>
                        <p className="text-gray-900">{notam.texto}</p>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-gray-500">Nenhum NOTAM ativo</p>
                )}
              </div>
            ))}
          </div>
        </div>
        
        {/* ATS Preview */}
        <div>
          <h4 className="text-lg font-medium text-gray-900 mb-4 pb-2 border-b border-gray-200">
            Mensagem ATS
          </h4>
          <pre className="bg-gray-50 p-4 rounded text-sm font-mono whitespace-pre-wrap">
            {atsPreview}
          </pre>
        </div>
      </div>
    </div>
  );
}
