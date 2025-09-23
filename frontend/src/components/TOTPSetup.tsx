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
  const [error, setError] = useState('');

  const handleGenerateQR = async () => {
    if (!email) {
      setError('Email é obrigatório para gerar QR Code');
      return;
    }
    setError('');
    try {
      await generateQR(email);
    } catch (error) {
      console.error('Erro ao gerar QR Code:', error);
      setError('Erro ao gerar QR Code. Tente novamente.');
    }
  };

  const handleVerifyCode = async () => {
    if (!codigo || codigo.length !== 6) {
      setError('Digite um código de 6 dígitos');
      return;
    }

    setError('');
    try {
      await verifyCode(codigo);
      if (sessionId) {
        onVerified(sessionId);
      }
    } catch (error) {
      console.error('Erro ao verificar código:', error);
      setError('Código inválido. Tente novamente.');
    }
  };

  if (disabled) {
    return (
      <div className="p-4 bg-aviation-gray-50 rounded-2xl border border-aviation-gray-200">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-aviation-gray-300 rounded-lg flex items-center justify-center">
            <svg className="w-4 h-4 text-aviation-gray-600" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z" clipRule="evenodd"/>
            </svg>
          </div>
          <div>
            <p className="text-sm font-medium text-aviation-navy-700">TOTP Desabilitado</p>
            <p className="text-xs text-aviation-gray-500">Autenticação de dois fatores não está ativa</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {!qrCode ? (
        <div className="text-center space-y-4">
          <div className="w-16 h-16 bg-gradient-aviation-red rounded-2xl flex items-center justify-center mx-auto">
            <svg className="w-8 h-8 text-aviation-white" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>
          <div>
            <h3 className="text-lg font-semibold text-aviation-navy-900 mb-2">Configurar Autenticador</h3>
            <p className="text-sm text-aviation-gray-600 mb-4">
              Configure a autenticação de dois fatores para maior segurança da sua conta
            </p>
          </div>
          <button
            onClick={handleGenerateQR}
            disabled={loading}
            className="btn btn-primary w-full"
          >
            {loading ? (
              <>
                <svg className="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                Gerando QR Code...
              </>
            ) : (
              <>
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z"/>
                </svg>
                Gerar QR Code do Autenticador
              </>
            )}
          </button>
        </div>
      ) : (
        <div className="space-y-6">
          {/* QR Code Section */}
          <div className="text-center space-y-4">
            <div className="w-16 h-16 bg-gradient-aviation-red rounded-2xl flex items-center justify-center mx-auto">
              <svg className="w-8 h-8 text-aviation-white" fill="currentColor" viewBox="0 0 24 24">
                <path d="M3 3h7v7H3V3zm0 11h7v7H3v-7zm11-11h7v7h-7V3zm0 11h7v7h-7v-7z"/>
              </svg>
            </div>
            <div>
              <h3 className="text-lg font-semibold text-aviation-navy-900 mb-2">Escaneie o QR Code</h3>
              <p className="text-sm text-aviation-gray-600 mb-4">
                Use seu app autenticador (Google Authenticator, Authy, etc.) para escanear o código
              </p>
            </div>
            {otpauthUrl && (
              <div className="flex justify-center p-4 bg-white rounded-2xl border border-aviation-gray-200 shadow-sm">
                <QrCode otpauthUrl={otpauthUrl} />
              </div>
            )}
          </div>

          {/* Verification Section */}
          {!verified ? (
            <div className="space-y-4">
              <div className="grid gap-2">
                <label htmlFor="totp-code" className="font-medium text-aviation-navy-700">
                  Código do Autenticador (6 dígitos)
                </label>
                <input
                  id="totp-code"
                  type="text"
                  value={codigo}
                  onChange={(e) => setCodigo(e.target.value.replace(/\D/g, '').slice(0, 6))}
                  placeholder="123456"
                  className="input text-center text-lg tracking-widest"
                  maxLength={6}
                />
                <p className="text-xs text-aviation-gray-500">
                  Digite o código de 6 dígitos gerado pelo seu app autenticador
                </p>
              </div>

              {error && (
                <div className="p-3 rounded-lg border border-red-300 bg-red-50 text-red-800 text-sm">
                  <div className="flex items-center gap-2">
                    <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                      <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clipRule="evenodd"/>
                    </svg>
                    {error}
                  </div>
                </div>
              )}

              <button
                onClick={handleVerifyCode}
                disabled={loading || codigo.length !== 6}
                className="btn btn-primary w-full"
              >
                {loading ? (
                  <>
                    <svg className="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                    </svg>
                    Validando...
                  </>
                ) : (
                  <>
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    Validar Código
                  </>
                )}
              </button>
            </div>
          ) : (
            <div className="text-center space-y-4">
              <div className="w-16 h-16 bg-gradient-to-r from-green-500 to-emerald-600 rounded-2xl flex items-center justify-center mx-auto">
                <svg className="w-8 h-8 text-white" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clipRule="evenodd"/>
                </svg>
              </div>
              <div>
                <h3 className="text-lg font-semibold text-green-800 mb-2">TOTP Verificado com Sucesso!</h3>
                <p className="text-sm text-green-600">
                  Sua autenticação de dois fatores está configurada e ativa
                </p>
              </div>
            </div>
          )}

          {/* Reset Button */}
          <button
            onClick={reset}
            className="btn btn-ghost w-full"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
            </svg>
            Gerar Novo QR Code
          </button>
        </div>
      )}
    </div>
  );
};
