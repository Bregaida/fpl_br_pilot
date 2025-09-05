import { useState, useCallback, useRef } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import type { FplForm, FplMode } from '@/types';
// import { composeFpl } from '@/services/fpl'; // Temporarily commented out as it's not being used

// Validation schema for the form
const formSchema = yup.object().shape({
  modo: yup.mixed<FplMode>().oneOf(['PVC', 'PVS']).required('Modo Ã© obrigatÃ³rio'),
  item7: yup.object().shape({
    identificacaoAeronave: yup.string().required('IdentificaÃ§Ã£o da aeronave Ã© obrigatÃ³ria'),
    indicativoChamada: yup.boolean(),
  }),
  item8: yup.object().shape({
    regrasVoo: yup.string().oneOf(['IFR', 'VFR', 'Y', 'Z']).required('Regra de voo Ã© obrigatÃ³ria'),
    tipoVoo: yup.string().oneOf(['G', 'S', 'N', 'M', 'X']).required('Tipo de voo Ã© obrigatÃ³rio'),
  }),
  item9: yup.object().shape({
    numero: yup.number().positive('NÃºmero deve ser positivo').integer('NÃºmero deve ser inteiro'),
    tipoAeronave: yup.string().required('Tipo de aeronave Ã© obrigatÃ³rio'),
    catTurbulencia: yup.string().oneOf(['L', 'M', 'H', 'J']).required('Categoria de turbulÃªncia Ã© obrigatÃ³ria'),
  }),
  item10A: yup.object(),
  item10B: yup.object(),
  item13: yup.object().shape({
    aerodromoPartida: yup.string().matches(/^[A-Z]{4}$/, 'CÃ³digo ICAO invÃ¡lido').required('AerÃ³dromo de partida Ã© obrigatÃ³rio'),
    hora: yup.string().matches(/^\d{4}$/, 'Hora invÃ¡lida (HHMM)').required('Hora Ã© obrigatÃ³ria'),
  }),
  item15: yup.object().shape({
    velocidadeCruzeiro: yup.string().matches(/^N\d{4}$/, 'Formato invÃ¡lido (ex: N0120)').required('Velocidade Ã© obrigatÃ³ria'),
    nivel: yup.string().matches(/^F\d{3}$/, 'NÃ­vel invÃ¡lido (ex: F080)').required('NÃ­vel Ã© obrigatÃ³rio'),
    rota: yup.string().required('Rota Ã© obrigatÃ³ria'),
  }),
  item16: yup.object().shape({
    aerodromoDestino: yup.string().matches(/^[A-Z]{4}$/, 'CÃ³digo ICAO invÃ¡lido').required('AerÃ³dromo de destino Ã© obrigatÃ³rio'),
    eetTotal: yup.string().matches(/^\d{4}$/, 'Tempo total invÃ¡lido (HHMM)').required('Tempo total Ã© obrigatÃ³rio'),
    alternado1: yup.string().matches(/^[A-Z]{4}$|^$/, 'CÃ³digo ICAO invÃ¡lido'),
    alternado2: yup.string().matches(/^[A-Z]{4}$|^$/, 'CÃ³digo ICAO invÃ¡lido'),
  }),
  item18: yup.object().shape({
    dof: yup.string().matches(/^\d{8}$/, 'Data invÃ¡lida (YYYYMMDD)').required('Data do voo Ã© obrigatÃ³ria'),
    rmk: yup.string(),
  }),
  item19: yup.object().shape({
    autonomia: yup.string().required('Autonomia Ã© obrigatÃ³ria'),
    pob: yup.number().required('POB Ã© obrigatÃ³rio'),
    radioEmergencia: yup.object(),
    sobrevivencia: yup.object(),
    coletes: yup.object(),
    botes: yup.object(),
    infoAdicionais: yup.object({
      corMarcaANV: yup.string().required('Cor/marcaÃ§Ã£o Ã© obrigatÃ³ria'),
      pilotoEmComando: yup.string().required('Piloto em comando Ã© obrigatÃ³rio'),
      codAnac1: yup.string().required('CÃ³digo ANAC 1 Ã© obrigatÃ³rio'),
      codAnac2: yup.string(),
      telefone: yup.string().required('Telefone Ã© obrigatÃ³rio'),
    }),
  }),
});

interface FplFormProps {
  onCompose: (data: any) => void;
  isComposing: boolean;
}

export default function FplForm({ onCompose, isComposing }: FplFormProps) {
  const [mode, setMode] = useState<FplMode>('PVC');
  // State variables removed as they were not being used
  
  const { control, handleSubmit, watch, setValue, formState: { errors } } = useForm<FplForm>({
    resolver: yupResolver(formSchema),
    defaultValues: {
      modo: 'PVC',
      item7: {
        identificacaoAeronave: '',
        indicativoChamada: false,
      },
      item8: { regrasVoo: 'IFR', tipoVoo: 'N' },
      item9: { tipoAeronave: '', catTurbulencia: 'L' },
      item10A: {},
      item10B: {},
      item13: { aerodromoPartida: '', hora: '' },
      item15: { velocidadeCruzeiro: 'N0120', nivel: 'F080', rota: '' },
      item16: { aerodromoDestino: '', eetTotal: '' },
      item18: { dof: new Date().toISOString().slice(0, 10).replace(/-/g, '') },
      item19: {
        autonomia: '0030',
        pob: 1,
        radioEmergencia: {},
        sobrevivencia: {},
        coletes: {},
        botes: {},
        infoAdicionais: {
          corMarcaANV: '',
          pilotoEmComando: '',
          codAnac1: '',
          telefone: '',
        },
      },
    },
  });

  // Watched fields removed as they were not being used

  // Handle form submission with debounce
  const debounceTimeout = useRef<ReturnType<typeof setTimeout>>();
  
  const handleFormChange = useCallback(() => {
    if (debounceTimeout.current) {
      clearTimeout(debounceTimeout.current);
    }
    
    debounceTimeout.current = setTimeout(async () => {
      const isValid = await formSchema.isValid(watch());
      if (isValid) {
        const formData = watch();
        onCompose(formData);
      }
    }, 600);
  }, [onCompose, watch]);

  // Toggle between PVC and PVS modes
  const toggleMode = () => {
    const newMode = mode === 'PVC' ? 'PVS' : 'PVC';
    setMode(newMode);
    setValue('modo', newMode);
    handleFormChange();
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold">Plano de Voo {mode}</h2>
        <button
          type="button"
          onClick={toggleMode}
          className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          Mudar para {mode === 'PVC' ? 'PVS' : 'PVC'}
        </button>
      </div>

      <form onChange={handleFormChange} className="space-y-6">
        {/* Item 7: IdentificaÃ§Ã£o da Aeronave */}
        <div className="bg-white p-4 rounded-lg shadow">
          <h3 className="text-lg font-semibold mb-4">7. IdentificaÃ§Ã£o da Aeronave</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">
                MarcaÃ§Ã£o/MatrÃ­cula
              </label>
              <Controller
                name="item7.identificacaoAeronave"
                control={control}
                render={({ field }) => (
                  <input
                    {...field}
                    type="text"
                    className={`mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm ${
                      errors.item7?.identificacaoAeronave ? 'border-red-500' : ''
                    }`}
                    placeholder="Ex: PRABC"
                  />
                )}
              />
              {errors.item7?.identificacaoAeronave && (
                <p className="mt-1 text-sm text-red-600">{errors.item7.identificacaoAeronave.message}</p>
              )}
            </div>
            <div className="flex items-end">
              <Controller
                name="item7.indicativoChamada"
                control={control}
                render={({ field: { value, ...field } }) => (
                  <div className="flex items-center">
                    <input
                      {...field}
                      type="checkbox"
                      id="indicativoChamada"
                      className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                      checked={!!value}
                      onChange={(e) => field.onChange(e.target.checked)}
                    />
                    <label htmlFor="indicativoChamada" className="ml-2 block text-sm text-gray-700">
                      Indicativo de Chamada
                    </label>
                  </div>
                )}
              />
            </div>
          </div>
        </div>

        {/* Add more form sections here following the same pattern */}
        
        <div className="flex justify-end">
          <button
            type="button"
            onClick={handleSubmit((data) => onCompose(data))}
            disabled={isComposing}
            className={`px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 ${
              isComposing ? 'opacity-50 cursor-not-allowed' : ''
            }`}
          >
            {isComposing ? 'Processando...' : 'Atualizar Plano de Voo'}
          </button>
        </div>
      </form>
    </div>
  );
}
