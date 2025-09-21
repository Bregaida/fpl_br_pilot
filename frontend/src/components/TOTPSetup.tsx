import React, { useState } from 'react';
import { useTOTP } from '../hooks/useTOTP';
import QrCode from './QrCode';

interface TOTPSetupProps {
  onVerified: (sessionId: string) => void;
  disabled?: boolean;
  email?: string;
}


export const TOTPSetup: React.FC<TOTPSetupProps> = ({ onVerified, disabled, email }) => {
  const { loading, qrCode, otpauthUrl, sessionId, verified, generateQR, verifyCode, reset } = useTOTP();
  const [codigo, setCodigo] = useState('');


  const handleGenerateQR = async () => {
    if (!email) {
      alert('Email é obrigatório para gerar QR Code');
      return;
    }
    try {
      await generateQR(email);
    } catch (error) {
      console.error('Erro ao gerar QR Code:', error);
      alert('Erro ao gerar QR Code. Tente novamente.');
    }
  };

  const handleVerifyCode = async () => {
    if (!codigo || codigo.length !== 6) {
      alert('Digite um código de 6 dígitos');
      return;
    }

    try {
      await verifyCode(codigo);
      if (sessionId) {
        onVerified(sessionId);
      }
    } catch (error) {
      console.error('Erro ao verificar código:', error);
      alert('Código inválido. Tente novamente.');
    }
  };

  if (disabled) {
    return (
      <div className="p-4 bg-gray-100 rounded-lg">
        <p className="text-sm text-gray-600">TOTP desabilitado</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      {!qrCode ? (
        <button
          onClick={handleGenerateQR}
          disabled={loading}
          className="w-full px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
        >
          {loading ? 'Gerando QR Code...' : 'Gerar QR Code do Autenticador'}
        </button>
      ) : (
        <div className="space-y-4">
          <div className="text-center">
            <p className="text-sm text-gray-600 mb-2">
              Escaneie o QR Code com seu app autenticador:
            </p>
            {otpauthUrl && (
              <div className="flex justify-center">
                <QrCode otpauthUrl={otpauthUrl} />
              </div>
            )}
          </div>

          {!verified ? (
            <div className="space-y-2">
              <label htmlFor="totp-code" className="block text-sm font-medium text-gray-700">
                Código do app (6 dígitos):
              </label>
              <input
                id="totp-code"
                type="text"
                value={codigo}
                onChange={(e) => setCodigo(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="123456"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                maxLength={6}
              />
              <button
                onClick={handleVerifyCode}
                disabled={loading || codigo.length !== 6}
                className="w-full px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:opacity-50"
              >
                {loading ? 'Validando...' : 'Validar Código'}
              </button>
            </div>
          ) : (
            <div className="text-center">
              <div className="inline-flex items-center px-4 py-2 bg-green-100 text-green-800 rounded-lg">
                <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
                TOTP Verificado ✓
              </div>
            </div>
          )}

          <button
            onClick={reset}
            className="w-full px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700"
          >
            Gerar Novo QR Code
          </button>
        </div>
      )}
    </div>
  );
};
