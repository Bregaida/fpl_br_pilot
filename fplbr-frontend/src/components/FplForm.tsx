import { useState, useEffect, useCallback, useRef } from 'react';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { FplForm, FplMode } from '@/types';
import { composeFpl } from '@/services/fpl';

// Validation schema for the form
const formSchema = yup.object().shape({
  modo: yup.mixed<FplMode>().oneOf(['PVC', 'PVS']).required('Modo é obrigatório'),
  item7: yup.object().shape({
    identificacaoAeronave: yup.string().required('Identificação da aeronave é obrigatória'),
    indicativoChamada: yup.boolean(),
  }),
  item8: yup.object().shape({
    regrasVoo: yup.string().oneOf(['IFR', 'VFR', 'Y', 'Z']).required('Regra de voo é obrigatória'),
    tipoVoo: yup.string().oneOf(['G', 'S', 'N', 'M', 'X']).required('Tipo de voo é obrigatório'),
  }),
  item9: yup.object().shape({
    numero: yup.number().positive('Número deve ser positivo').integer('Número deve ser inteiro'),
    tipoAeronave: yup.string().required('Tipo de aeronave é obrigatório'),
    catTurbulencia: yup.string().oneOf(['L', 'M', 'H', 'J']).required('Categoria de turbulência é obrigatória'),
  }),
  item10A: yup.object(),
  item10B: yup.object(),
  item13: yup.object().shape({
    aerodromoPartida: yup.string().matches(/^[A-Z]{4}$/, 'Código ICAO inválido').required('Aeródromo de partida é obrigatório'),
    hora: yup.string().matches(/^\d{4}$/, 'Hora inválida (HHMM)').required('Hora é obrigatória'),
  }),
  item15: yup.object().shape({
    velocidadeCruzeiro: yup.string().matches(/^N\d{4}$/, 'Formato inválido (ex: N0120)').required('Velocidade é obrigatória'),
    nivel: yup.string().matches(/^F\d{3}$/, 'Nível inválido (ex: F080)').required('Nível é obrigatório'),
    rota: yup.string().required('Rota é obrigatória'),
  }),
  item16: yup.object().shape({
    aerodromoDestino: yup.string().matches(/^[A-Z]{4}$/, 'Código ICAO inválido').required('Aeródromo de destino é obrigatório'),
    eetTotal: yup.string().matches(/^\d{4}$/, 'Tempo total inválido (HHMM)').required('Tempo total é obrigatório'),
    alternado1: yup.string().matches(/^[A-Z]{4}$|^$/, 'Código ICAO inválido'),
    alternado2: yup.string().matches(/^[A-Z]{4}$|^$/, 'Código ICAO inválido'),
  }),
  item18: yup.object().shape({
    dof: yup.string().matches(/^\d{8}$/, 'Data inválida (YYYYMMDD)').required('Data do voo é obrigatória'),
    rmk: yup.string(),
  }),
  item19: yup.object().shape({
    autonomia: yup.string().required('Autonomia é obrigatória'),
    pob: yup.number().required('POB é obrigatório'),
    radioEmergencia: yup.object(),
    sobrevivencia: yup.object(),
    coletes: yup.object(),
    botes: yup.object(),
    infoAdicionais: yup.object({
      corMarcaANV: yup.string().required('Cor/marcação é obrigatória'),
      pilotoEmComando: yup.string().required('Piloto em comando é obrigatório'),
      codAnac1: yup.string().required('Código ANAC 1 é obrigatório'),
      codAnac2: yup.string(),
      telefone: yup.string().required('Telefone é obrigatório'),
    }),
  }),
});

interface FplFormProps {
  onCompose: (data: any) => void;
  isComposing: boolean;
}

export default function FplForm({ onCompose, isComposing }: FplFormProps) {
  const [mode, setMode] = useState<FplMode>('PVC');
  const [showSobrevivencia, setShowSobrevivencia] = useState(false);
  const [showColetes, setShowColetes] = useState(false);
  const [showBotes, setShowBotes] = useState(false);
  
  const { control, handleSubmit, watch, setValue, formState: { errors } } = useForm<FplForm>({
    resolver: yupResolver(formSchema),
    defaultValues: {
      modo: 'PVC',
      item7: { identificacaoAeronave: '', indicativoChamada: false },
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

  // Watch for changes in specific fields to update UI state
  const sobrevivenciaS = watch('item19.sobrevivencia.S');
  const sobrevivenciaJ = watch('item19.sobrevivencia.J');
  const botesD = watch('item19.botes.D');

  // Update UI state when watched fields change
  useEffect(() => {
    setShowSobrevivencia(!!sobrevivenciaS);
  }, [sobrevivenciaS]);

  useEffect(() => {
    setShowColetes(!!sobrevivenciaJ);
  }, [sobrevivenciaJ]);

  useEffect(() => {
    setShowBotes(!!botesD);
  }, [botesD]);

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
        {/* Item 7: Identificação da Aeronave */}
        <div className="bg-white p-4 rounded-lg shadow">
          <h3 className="text-lg font-semibold mb-4">7. Identificação da Aeronave</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700">
                Marcação/Matrícula
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
                render={({ field }) => (
                  <div className="flex items-center">
                    <input
                      {...field}
                      type="checkbox"
                      id="indicativoChamada"
                      className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                      checked={!!field.value}
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
